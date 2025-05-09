package backend.eventsphere.promo.service;

import backend.eventsphere.promo.factory.PromoFactory;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.repository.KodePromoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class KodePromoService {

    private final KodePromoRepository repository;
    private final PromoFactory promoFactory;

    public KodePromoService(KodePromoRepository repository, PromoFactory promoFactory) {
        this.repository = repository;
        this.promoFactory = promoFactory;
    }

    public KodePromo createPercentagePromo(String code, BigDecimal percentage,
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

        return repository.save(promo);
    }

    public KodePromo createFixedAmountPromo(String code, BigDecimal fixedAmount,
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

        return repository.save(promo);
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

    public KodePromo updatePromo(UUID id, String newCode, BigDecimal newDiscount,
                                 KodePromo.DiscountType newDiscountType,
                                 LocalDate newStartDate, LocalDate newEndDate) {
        KodePromo promo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promo not found"));

        validatePromoUpdate(promo, newCode, newDiscount, newDiscountType);

        promo.setCode(newCode);
        promo.setDiscount(newDiscount);
        promo.setDiscountType(newDiscountType);
        promo.setStartDate(newStartDate);
        promo.setEndDate(newEndDate);

        return repository.save(promo);
    }

    private void validatePromoUpdate(KodePromo existingPromo, String newCode,
                                     BigDecimal newDiscount, KodePromo.DiscountType newDiscountType) {
        if (!existingPromo.getCode().equalsIgnoreCase(newCode) && repository.existsByCodeIgnoreCase(newCode)) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        if (newDiscountType == KodePromo.DiscountType.FIXED_AMOUNT && newDiscount.scale() > 0) {
            throw new IllegalArgumentException("Diskon nominal tetap harus dalam bilangan bulat");
        }

        if (newDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount harus lebih besar dari 0");
        }
    }

    @Transactional(readOnly = true)
    public boolean isPromoCodeExists(String code) {
        return repository.existsByCodeIgnoreCase(code);
    }
}