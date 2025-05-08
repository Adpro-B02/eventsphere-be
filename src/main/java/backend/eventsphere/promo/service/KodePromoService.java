package backend.eventsphere.promo.service;

import backend.eventsphere.promo.factory.PromoFactory;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.repository.KodePromoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KodePromoService {

    private final KodePromoRepository repository;
    private final PromoFactory promoFactory;

    public KodePromoService(KodePromoRepository repository, PromoFactory promoFactory) {
        this.repository = repository;
        this.promoFactory = promoFactory;
    }

    public KodePromo createPromo(String code, BigDecimal discount, KodePromo.DiscountType discountType,
                                 LocalDate startDate, LocalDate endDate, UUID eventId, UUID userId) {

        validatePromoCreation(code, discount, discountType, startDate, endDate);

        KodePromo promo = promoFactory.createPromo(
                code,
                discount,
                discountType,
                startDate,
                endDate,
                eventId,
                userId
        );

        return repository.save(promo);
    }

    private void validatePromoCreation(String code, BigDecimal discount,
                                       KodePromo.DiscountType discountType,
                                       LocalDate startDate, LocalDate endDate) {
        if (repository.existsByCode(code)) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Tanggal berakhir tidak boleh sebelum tanggal mulai");
        }

        if (discountType == KodePromo.DiscountType.FIXED_AMOUNT && discount.scale() > 0) {
            throw new IllegalArgumentException("Diskon nominal tetap harus dalam bilangan bulat");
        }

        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount harus lebih besar dari 0");
        }
    }

    public Optional<KodePromo> getPromoById(UUID id) {
        return repository.findPromoById(id);
    }

    public Optional<KodePromo> getPromoByCode(String code) {
        return repository.findPromoByCode(code);
    }

    public List<KodePromo> getPromosByEvent(UUID eventId) {
        return repository.findAllByEventId(eventId);
    }

    public List<KodePromo> getActivePromosByEvent(UUID eventId) {
        return repository.findActivePromos(eventId, LocalDate.now());
    }

    public void deletePromo(UUID id) {
        repository.deletePromoById(id);
    }

    public KodePromo updatePromo(UUID id, String newCode, BigDecimal newDiscount,
                                 KodePromo.DiscountType newDiscountType,
                                 LocalDate newStartDate, LocalDate newEndDate) {
        KodePromo promo = repository.findPromoById(id)
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
        if (!existingPromo.getCode().equalsIgnoreCase(newCode) && repository.existsByCode(newCode)) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        if (newDiscountType == KodePromo.DiscountType.FIXED_AMOUNT && newDiscount.scale() > 0) {
            throw new IllegalArgumentException("Diskon nominal tetap harus dalam bilangan bulat");
        }

        if (newDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount harus lebih besar dari 0");
        }
    }

    public boolean isPromoCodeExists(String code) {
        return repository.existsByCode(code);
    }
}