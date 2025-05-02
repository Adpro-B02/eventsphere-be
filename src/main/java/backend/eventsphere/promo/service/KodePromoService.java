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

    public KodePromo createPromo(String code, BigDecimal discount, LocalDate startDate,
                                 LocalDate endDate, UUID eventId, UUID userId) {

        Optional<KodePromo> existingPromo = repository.findPromoByCode(code);
        if (existingPromo.isPresent()) {
            throw new IllegalArgumentException("Kode promo sudah digunakan");
        }

        KodePromo promo = promoFactory.createPromo(code, discount, startDate, endDate, eventId, userId);
        return repository.save(promo);
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

    public void deletePromo(UUID id) {
        repository.deletePromoById(id);
    }

    public KodePromo updatePromo(UUID id, String newCode, BigDecimal newDiscount,
                                 LocalDate newStartDate, LocalDate newEndDate) {
        Optional<KodePromo> existingPromo = repository.findPromoById(id);
        if (existingPromo.isEmpty()) {
            throw new IllegalArgumentException("Promo not found");
        }

        KodePromo promo = existingPromo.get();
        promo.setCode(newCode);
        promo.setDiscount(newDiscount);
        promo.setStartDate(newStartDate);
        promo.setEndDate(newEndDate);

        return repository.save(promo);
    }
}