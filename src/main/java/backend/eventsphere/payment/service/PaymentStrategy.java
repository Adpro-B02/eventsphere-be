package backend.eventsphere.payment.service;

public interface PaymentStrategy {
    void pay(double amount);
}

