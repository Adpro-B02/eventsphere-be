package backend.eventsphere.repository;

import backend.eventsphere.model.KodePromo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KodePromoRepository {
    KodePromo save(KodePromo promo);
    Optional<KodePromo> findPromoById(UUID id);
    Optional<KodePromo> findPromoByCode(String code);
    List<KodePromo> findAllByEventId(UUID eventId);
    void deletePromoById(UUID id);
}