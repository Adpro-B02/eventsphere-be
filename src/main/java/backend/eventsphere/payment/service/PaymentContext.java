package backend.eventsphere.payment.service;

import backend.eventsphere.payment.model.PaymentRequest;
import backend.eventsphere.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class PaymentContext {

    private final Map<String, PaymentStrategy> strategyMap;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentContext(Map<String, PaymentStrategy> strategyMap,
                          PaymentRepository paymentRepository) {
        this.strategyMap = strategyMap;
        this.paymentRepository = paymentRepository;
    }

    public void pay(String method, double amount) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Payment method " + method + " is not supported.");
        }

        strategy.pay(amount);

        PaymentRequest tx = new PaymentRequest(null, method, amount, LocalDateTime.now());
        paymentRepository.save(tx);
    }

    public List<PaymentRequest> getAllTransactions() {
        return paymentRepository.findAll();
    }
}
