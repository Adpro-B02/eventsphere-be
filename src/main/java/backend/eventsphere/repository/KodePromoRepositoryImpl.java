package backend.eventsphere.repository;

import backend.eventsphere.model.KodePromo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KodePromoRepositoryImpl implements KodePromoRepository {

    private final Map<UUID, KodePromo> storage = new ConcurrentHashMap<>();

    @Override
    public KodePromo save(KodePromo promo) {
        storage.put(promo.getId(), promo);
        return promo;
    }

    @Override
    public Optional<KodePromo> findPromoById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<KodePromo> findPromoByCode(String code) {
        return storage.values()
                .stream()
                .filter(promo -> promo.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public List<KodePromo> findAllByEventId(UUID eventId) {
        return storage.values()
                .stream()
                .filter(promo -> promo.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromoById(UUID id) {
        storage.remove(id);
    }
}