package backend.eventsphere.payment.service;

import org.springframework.stereotype.Component;

@Component("credit_card")
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid Rp" + amount + " with Credit Card.");
    }
}
