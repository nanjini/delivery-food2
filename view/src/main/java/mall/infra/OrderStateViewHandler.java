package mall.infra;

import mall.domain.*;
import mall.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrderStateViewHandler {

    @Autowired
    private OrderStateRepository orderStateRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderPlaced_then_CREATE_1 (@Payload OrderPlaced orderPlaced) {
        try {

            if (!orderPlaced.validate()) return;

            // view 객체 생성
            OrderState orderState = new OrderState();
            // view 객체에 이벤트의 Value 를 set 함
            orderState.setId(orderPlaced.getId());
            orderState.setItem(orderPlaced.getItem());
            orderState.setOrderState("시작");
            // view 레파지 토리에 save
            orderStateRepository.save(orderState);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentApproved_then_UPDATE_1(@Payload PaymentApproved paymentApproved) {
        try {
            if (!paymentApproved.validate()) return;
                // view 객체 조회
            Optional<OrderState> orderStateOptional = orderStateRepository.findById(paymentApproved.getId());

            if( orderStateOptional.isPresent()) {
                 OrderState orderState = orderStateOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                orderState.setPayState("결제시작");    
                // view 레파지 토리에 save
                 orderStateRepository.save(orderState);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderPlaced_then_DELETE_1(@Payload OrderPlaced orderPlaced) {
        try {
            if (!orderPlaced.validate()) return;
            // view 레파지 토리에 삭제 쿼리
            orderStateRepository.deleteById(orderPlaced.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

