package backend.eventsphere.payment.controller;

import backend.eventsphere.payment.model.PaymentRequest;
import backend.eventsphere.payment.service.PaymentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentContext paymentContext;

    @PostMapping
    public String pay(@RequestBody PaymentRequest request) {
        paymentContext.pay(request.getMethod(), request.getAmount());
        return "Payment successful with " + request.getMethod();
    }

    @GetMapping("/history")
    public List<PaymentRequest> getHistory() {
        return paymentContext.getAllTransactions();
    }
}
