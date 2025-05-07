package backend.eventsphere.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    // Getters & Setters
    private Long id;
    private String method;
    private double amount;
    private LocalDateTime timestamp;
}
