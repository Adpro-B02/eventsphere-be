package backend.eventsphere.payment.repository;

import backend.eventsphere.payment.model.PaymentRequest;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PaymentRepository {

    private final Map<Long, PaymentRequest> storage = new HashMap<>();
    private Long currentId = 1L;

    public PaymentRequest save(PaymentRequest tx) {
        tx.setId(currentId++);
        storage.put(tx.getId(), tx);
        return tx;
    }

    public List<PaymentRequest> findAll() {
        return new ArrayList<>(storage.values());
    }
}

