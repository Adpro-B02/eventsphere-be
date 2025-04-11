package backend.eventsphere.service;

import backend.eventsphere.model.KodePromo;
import backend.eventsphere.repository.KodePromoRepository;
import backend.eventsphere.factory.PromoFactory;
import backend.eventsphere.factory.DiskonPromoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KodePromoServiceTest {

    private KodePromoRepository repository;
    private KodePromoService service;
    private UUID promoId;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        repository = mock(KodePromoRepository.class);
        PromoFactory factory = new DiskonPromoFactory();
        service = new KodePromoService(repository, factory);

        promoId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void testCreatePromo() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);

        KodePromo promo = new KodePromo(null, "DISC10", new BigDecimal("0.1"), start, end, eventId, userId);

        when(repository.save(any(KodePromo.class))).thenReturn(promo);

        KodePromo created = service.createPromo("DISC10", new BigDecimal("0.1"), start, end, eventId, userId);

        assertEquals("DISC10", created.getCode());
        verify(repository, times(1)).save(any(KodePromo.class));
    }

    @Test
    void testGetPromoByIdFound() {
        KodePromo promo = new KodePromo(promoId, "CODE", new BigDecimal("0.2"),
                LocalDate.now(), LocalDate.now().plusDays(5), eventId, userId);
        when(repository.findPromoById(promoId)).thenReturn(Optional.of(promo));

        Optional<KodePromo> found = service.getPromoById(promoId);

        assertTrue(found.isPresent());
        assertEquals("CODE", found.get().getCode());
    }

    @Test
    void testGetPromoByIdNotFound() {
        when(repository.findPromoById(promoId)).thenReturn(Optional.empty());

        Optional<KodePromo> found = service.getPromoById(promoId);

        assertTrue(found.isEmpty());
    }

    @Test
    void testUpdatePromo() {
        KodePromo existing = new KodePromo(promoId, "OLDCODE", new BigDecimal("0.1"),
                LocalDate.now(), LocalDate.now().plusDays(3), eventId, userId);
        when(repository.findPromoById(promoId)).thenReturn(Optional.of(existing));
        when(repository.save(any(KodePromo.class))).thenReturn(existing);

        KodePromo updated = service.updatePromo(promoId, "NEWCODE", new BigDecimal("0.3"),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));

        assertEquals("NEWCODE", updated.getCode());
        assertEquals(new BigDecimal("0.3"), updated.getDiscount());
    }

    @Test
    void testUpdatePromo_NotFound() {
        when(repository.findPromoById(promoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.updatePromo(promoId, "X", BigDecimal.ONE, LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testDeletePromo() {
        service.deletePromo(promoId);
        verify(repository, times(1)).deletePromoById(promoId);
    }

    @Test
    void testGetPromosByEvent() {
        List<KodePromo> promos = List.of(
                new KodePromo(null, "A", BigDecimal.ONE, LocalDate.now(), LocalDate.now(), eventId, userId)
        );
        when(repository.findAllByEventId(eventId)).thenReturn(promos);

        List<KodePromo> result = service.getPromosByEvent(eventId);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getCode());
    }
}