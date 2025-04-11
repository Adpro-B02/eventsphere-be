package backend.eventsphere.service;

import backend.eventsphere.model.KodePromo;
import backend.eventsphere.repository.KodePromoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KodePromoService {

    private final KodePromoRepository repository;

    public KodePromoService(KodePromoRepository repository) {
        this.repository = repository;
    }

    public KodePromo createPromo(String code, BigDecimal discount, LocalDate startDate,
                                 LocalDate endDate, UUID eventId, UUID createdBy) {
        KodePromo promo = new KodePromo(null, code, discount, startDate, endDate, eventId, createdBy);
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