package backend.eventsphere.promo.controller;

import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.service.KodePromoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ResponseEntity<KodePromo> createPromo(
            @RequestParam String code,
            @RequestParam BigDecimal amount,
            @RequestParam String promoType,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam UUID eventId,
            @RequestParam UUID createdBy) {

        KodePromo created;
        if ("percentage".equalsIgnoreCase(promoType)) {
            created = promoService.createPercentagePromo(
                    code, amount, startDate, endDate, eventId, createdBy);
        } else {
            created = promoService.createFixedAmountPromo(
                    code, amount, startDate, endDate, eventId, createdBy);
        }
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

    @GetMapping("/active/event/{eventId}")
    public ResponseEntity<List<KodePromo>> getActivePromosByEvent(@PathVariable UUID eventId) {
        List<KodePromo> promos = promoService.getActivePromosByEvent(eventId);
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
            @RequestParam(required = false) String code,
            @RequestParam(required = false) BigDecimal discount,
            @RequestParam(required = false) KodePromo.DiscountType discountType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        KodePromo updated = promoService.updatePromo(
                id,
                code,
                discount,
                discountType,
                startDate,
                endDate
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromo(@PathVariable UUID id) {
        promoService.deletePromo(id);
        return ResponseEntity.noContent().build();
    }
}