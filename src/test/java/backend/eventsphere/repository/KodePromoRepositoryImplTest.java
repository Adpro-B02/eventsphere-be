package backend.eventsphere.repository;

import backend.eventsphere.model.KodePromo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class KodePromoRepositoryImplTest {

    private KodePromoRepository repository;
    private KodePromo promo;

    @BeforeEach
    void setUp() {
        repository = new KodePromoRepositoryImpl();
        promo = new KodePromo(
                UUID.randomUUID(),
                "DISKON10",
                new BigDecimal("10.00"),
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Test
    void testSaveAndFindById() {
        repository.save(promo);
        Optional<KodePromo> result = repository.findPromoById(promo.getId());
        assertTrue(result.isPresent());
        assertEquals(promo.getCode(), result.get().getCode());
    }

    @Test
    void testFindByCode() {
        repository.save(promo);
        Optional<KodePromo> result = repository.findPromoByCode("DISKON10");
        assertTrue(result.isPresent());
        assertEquals(promo.getId(), result.get().getId());
    }

    @Test
    void testFindAllByEventId() {
        UUID eventId = promo.getEventId();
        repository.save(promo);

        KodePromo promo2 = new KodePromo(UUID.randomUUID(), "DISKON20",
                new BigDecimal("20.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                eventId,
                UUID.randomUUID());

        repository.save(promo2);

        List<KodePromo> promos = repository.findAllByEventId(eventId);
        assertEquals(2, promos.size());
    }

    @Test
    void testDeleteById() {
        repository.save(promo);
        repository.deletePromoById(promo.getId());

        assertFalse(repository.findPromoById(promo.getId()).isPresent());
    }
}