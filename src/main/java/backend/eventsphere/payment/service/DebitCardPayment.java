package backend.eventsphere.payment.service;


import org.springframework.stereotype.Component;

@Component("debit_card")
public class DebitCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid Rp" + amount + " using Debit Card.");
    }
}
