package backend.eventsphere.promo.service;

import backend.eventsphere.promo.factory.PromoFactory;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.repository.KodePromoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Component
public class KodePromoService {

    private final KodePromoRepository repository;
    private final PromoFactory promoFactory;

    public KodePromoService(KodePromoRepository repository, PromoFactory promoFactory) {
        this.repository = repository;
        this.promoFactory = promoFactory;
    }

    @Async
    public CompletableFuture<KodePromo> createPercentagePromo(String code, BigDecimal percentage,
                                           LocalDate startDate, LocalDate endDate,
                                           UUID eventId, UUID userId) {

        validatePromoCreation(code, startDate, endDate);

        KodePromo promo = promoFactory.createPercentageDiscount(
                code,
                percentage,
                startDate,
                endDate,
                eventId,
                userId
        );

        return CompletableFuture.completedFuture(repository.save(promo));
    }

    @Async
    public CompletableFuture<KodePromo> createFixedAmountPromo(String code, BigDecimal fixedAmount,
                                            LocalDate startDate, LocalDate endDate,
                                            UUID eventId, UUID userId) {

        validatePromoCreation(code, startDate, endDate);

        KodePromo promo = promoFactory.createFixedAmountDiscount(
                code,
                fixedAmount,
                startDate,
                endDate,
                eventId,
                userId
        );

        return CompletableFuture.completedFuture(repository.save(promo));
    }

    private void validatePromoCreation(String code,
                                       LocalDate startDate, LocalDate endDate) {
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Tanggal berakhir tidak boleh sebelum tanggal mulai");
        }
    }

    @Transactional(readOnly = true)
    public Optional<KodePromo> getPromoById(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<KodePromo> getPromoByCode(String code) {
        return repository.findByCodeIgnoreCase(code);
    }

    @Transactional(readOnly = true)
    public List<KodePromo> getPromosByEvent(UUID eventId) {
        return repository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<KodePromo> getActivePromosByEvent(UUID eventId) {
        return repository.findActivePromos(eventId, LocalDate.now());
    }

    public void deletePromo(UUID id) {
        repository.deleteById(id);
    }

    @Async
    public CompletableFuture<KodePromo> updatePromo(UUID id, String newCode, BigDecimal newDiscount,
                                 KodePromo.DiscountType newDiscountType,
                                 LocalDate newStartDate, LocalDate newEndDate) {
        KodePromo promo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promo not found"));

        validatePromoUpdate(promo, newCode, newDiscount, newDiscountType);

        if (newCode != null) promo.setCode(newCode);

        if (newDiscount != null) {
            if (newDiscountType == KodePromo.DiscountType.PERCENTAGE) {
                promo.setDiscount(newDiscount.divide(BigDecimal.valueOf(100)));
            } else {
                promo.setDiscount(newDiscount);
            }
        }
        if (newDiscountType != null) promo.setDiscountType(newDiscountType);
        if (newStartDate != null) promo.setStartDate(newStartDate);
        if (newEndDate != null) promo.setEndDate(newEndDate);


        return CompletableFuture.completedFuture(repository.save(promo));
    }

    private void validatePromoUpdate(KodePromo existingPromo, String newCode,
                                     BigDecimal newDiscount, KodePromo.DiscountType newDiscountType) {
        if (newCode != null &&
                !existingPromo.getCode().equalsIgnoreCase(newCode) &&
                repository.existsByCodeIgnoreCase(newCode)) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        if (newDiscount != null) {
            if (newDiscountType == KodePromo.DiscountType.FIXED_AMOUNT && newDiscount.scale() > 0) {
                throw new IllegalArgumentException("Diskon nominal tetap harus dalam bilangan bulat");
            }

            if (newDiscount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Diskon harus lebih besar dari 0");
            }

            if (newDiscountType == KodePromo.DiscountType.PERCENTAGE &&
                    newDiscount.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Diskon persentase maksimal 100%");
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean isPromoCodeExists(String code) {
        return repository.existsByCodeIgnoreCase(code);
    }
}