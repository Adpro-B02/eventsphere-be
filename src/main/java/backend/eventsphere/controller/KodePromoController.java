package backend.eventsphere.controller;

import backend.eventsphere.model.KodePromo;
import backend.eventsphere.service.KodePromoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promos")
public class KodePromoController {

    private final KodePromoService promoService;

    public KodePromoController(KodePromoService promoService) {
        this.promoService = promoService;
    }

    @PostMapping
    public ResponseEntity<KodePromo> createPromo(@RequestBody KodePromo promo) {
        KodePromo created = promoService.createPromo(
                promo.getCode(),
                promo.getDiscount(),
                promo.getStartDate(),
                promo.getEndDate(),
                promo.getEventId(),
                promo.getCreatedBy()
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KodePromo> getPromoById(@PathVariable UUID id) {
        return promoService.getPromoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<KodePromo>> getPromosByEventId(@PathVariable UUID eventId) {
        List<KodePromo> promos = promoService.getPromosByEvent(eventId);
        return ResponseEntity.ok(promos);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<KodePromo> getPromoByCode(@PathVariable String code) {
        return promoService.getPromoByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<KodePromo> updatePromo(
            @PathVariable UUID id,
            @RequestBody KodePromo promo) {
        KodePromo updated = promoService.updatePromo(
                id,
                promo.getCode(),
                promo.getDiscount(),
                promo.getStartDate(),
                promo.getEndDate()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromo(@PathVariable UUID id) {
        promoService.deletePromo(id);
        return ResponseEntity.noContent().build();
    }
}