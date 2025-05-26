package backend.eventsphere.promo.controller;

import backend.eventsphere.auth.model.User;
import backend.eventsphere.auth.repository.UserRepository;
import backend.eventsphere.promo.model.KodePromo;
import backend.eventsphere.promo.service.KodePromoService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/promos")
@AllArgsConstructor
public class KodePromoController {

    private final KodePromoService promoService;
    private final UserRepository userRepository;

    @PostMapping()
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<KodePromo> createPromo(
            @RequestParam("code") String code,
            @RequestParam("amount") Integer amount,
            @RequestParam("promoType") String promoType,
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam("eventId") String eventId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID userId = user.getId();

        if ("percentage".equalsIgnoreCase(promoType)) {
            BigDecimal percentage = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100));

            promoService.createPercentagePromo(code, percentage, startDate, endDate,
                    UUID.fromString(eventId), userId
            ).thenApply(ResponseEntity::ok);

            return ResponseEntity.ok(KodePromo.builder()
                    .code(code)
                    .discountType(KodePromo.DiscountType.PERCENTAGE)
                    .discount(percentage)
                    .startDate(startDate)
                    .endDate(endDate)
                    .eventId(UUID.fromString(eventId))
                    .createdBy(userId)
                    .build());

        } else {
            promoService.createFixedAmountPromo(code, BigDecimal.valueOf(amount), startDate, endDate,
                    UUID.fromString(eventId), userId
            ).thenApply(ResponseEntity::ok);

            return ResponseEntity.ok(KodePromo.builder()
                    .code(code)
                    .discountType(KodePromo.DiscountType.FIXED_AMOUNT)
                    .discount(BigDecimal.valueOf(amount))
                    .startDate(startDate)
                    .endDate(endDate)
                    .eventId(UUID.fromString(eventId))
                    .createdBy(userId)
                    .build());
        }
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ATTENDEE', 'ORGANIZER')")
    public ResponseEntity<List<KodePromo>> getAllPromos() {
        List<KodePromo> promos = promoService.getAllPromos();
        return ResponseEntity.ok(promos);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<KodePromo> getPromoById(@PathVariable UUID id) {
        return promoService.getPromoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<List<KodePromo>> getPromosByEventId(@PathVariable UUID eventId) {
        List<KodePromo> promos = promoService.getPromosByEvent(eventId);
        return ResponseEntity.ok(promos);
    }

    @GetMapping("/active/event/{eventId}")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<List<KodePromo>> getActivePromosByEvent(@PathVariable UUID eventId) {
        List<KodePromo> promos = promoService.getActivePromosByEvent(eventId);
        return ResponseEntity.ok(promos);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('ATTENDEE')")
    public ResponseEntity<KodePromo> getPromoByCode(@PathVariable String code) {
        return promoService.getPromoByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public CompletableFuture<ResponseEntity<KodePromo>> updatePromo(
            @PathVariable UUID id,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) BigDecimal discount,
            @RequestParam(required = false) KodePromo.DiscountType discountType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        return promoService.updatePromo(id, code, discount, discountType, startDate, endDate)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<Void> deletePromo(@PathVariable UUID id) {
        promoService.deletePromo(id);
        return ResponseEntity.noContent().build();
    }
}