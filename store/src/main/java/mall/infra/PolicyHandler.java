package mall.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import mall.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import mall.domain.*;

@Service
@Transactional
public class PolicyHandler{
    @Autowired OrderManagementRepository orderManagementRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='PaymentApproved'")
    public void wheneverPaymentApproved_Receipt(@Payload PaymentApproved paymentApproved){

        PaymentApproved event = paymentApproved;
        System.out.println("\n\n##### listener Receipt : " + paymentApproved + "\n\n");


        

        // Sample Logic //
        OrderManagement.receipt(event);
        

        

    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='PaymentCanceled'")
    public void wheneverPaymentCanceled_CancelReceipt(@Payload PaymentCanceled paymentCanceled){

        PaymentCanceled event = paymentCanceled;
        System.out.println("\n\n##### listener CancelReceipt : " + paymentCanceled + "\n\n");


        

        // Sample Logic //
        OrderManagement.cancelReceipt(event);
        

        

    }


    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderComplete'")
    public void wheneverOrderComplete_OrderManagementStateComplete(@Payload OrderComplete orderComplete){

        OrderComplete event = orderComplete;
        System.out.println("\n\n##### listener OrderManagementStateComplete : " + orderComplete + "\n\n");


        

        // Sample Logic //
        OrderManagement.orderManagementStateComplete(event);
        

        

    }

}


