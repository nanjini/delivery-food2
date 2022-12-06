package mall.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceFallBack implements PaymentService {
    private static Logger logger = LoggerFactory.getLogger(PaymentServiceFallBack.class);

    @Override
    public void pay(Payment payment) {
        logger.error("Circuit breaker has been opened. Fallback returned instead.");
    }
}