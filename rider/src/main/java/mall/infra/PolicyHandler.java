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
    @Autowired DeliveryRepository deliveryRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderManagementStateCompleted'")
    public void wheneverOrderManagementStateCompleted_DeliveryReceipt(@Payload OrderManagementStateCompleted orderManagementStateCompleted){

        OrderManagementStateCompleted event = orderManagementStateCompleted;
        System.out.println("\n\n##### listener DeliveryReceipt : " + orderManagementStateCompleted + "\n\n");


        

        // Sample Logic //
        Delivery.deliveryReceipt(event);
        

        

    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderComplete'")
    public void wheneverOrderComplete_DeliveryStateComplete(@Payload OrderComplete orderComplete){

        OrderComplete event = orderComplete;
        System.out.println("\n\n##### listener DeliveryStateComplete : " + orderComplete + "\n\n");


        

        // Sample Logic //
        Delivery.deliveryStateComplete(event);
        

        

    }

}


