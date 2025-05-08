package backend.eventsphere.promo.repository;

import backend.eventsphere.promo.model.KodePromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KodePromoRepository extends JpaRepository<KodePromo, UUID> {
    Optional<KodePromo> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<KodePromo> findByEventId(UUID eventId);
    @Query("SELECT k FROM KodePromo k WHERE k.eventId = :eventId AND " +
            "k.startDate <= :today AND k.endDate >= :today")
    List<KodePromo> findActivePromos(@Param("eventId") UUID eventId,
                                     @Param("today") LocalDate today);
}