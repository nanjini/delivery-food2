package mall.external;

import org.springframework.web.bind.annotation.RequestBody;

public class PaymentServiceFallBack implements PaymentService {

    @Override
    public void pay(Payment payment) {
        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
    }
}