package mall.domain;

import mall.domain.OrderManagementAccepted;
import mall.domain.OrderManagementCanceled;
import mall.domain.OrderManagementStateCompleted;
import mall.domain.OrderManagementStateStarted;
import mall.StoreApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="OrderManagement_table")
@Data

public class OrderManagement  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private Long orderId;
    
    
    
    
    
    private String address;
    
    
    
    
    
    private String foodType;
    
    
    
    
    
    private String state;

    @PostPersist
    public void onPostPersist(){


        OrderManagementAccepted orderManagementAccepted = new OrderManagementAccepted(this);
        orderManagementAccepted.publishAfterCommit();



        OrderManagementCanceled orderManagementCanceled = new OrderManagementCanceled(this);
        orderManagementCanceled.publishAfterCommit();



        OrderManagementStateCompleted orderManagementStateCompleted = new OrderManagementStateCompleted(this);
        orderManagementStateCompleted.publishAfterCommit();



        OrderManagementStateStarted orderManagementStateStarted = new OrderManagementStateStarted(this);
        orderManagementStateStarted.publishAfterCommit();

    }

    public static OrderManagementRepository repository(){
        OrderManagementRepository orderManagementRepository = StoreApplication.applicationContext.getBean(OrderManagementRepository.class);
        return orderManagementRepository;
    }




    public static void receipt(PaymentApproved paymentApproved){

        /** Example 1:  new item 
        OrderManagement orderManagement = new OrderManagement();
        repository().save(orderManagement);

        */

        /** Example 2:  finding and process
        
        repository().findById(paymentApproved.get???()).ifPresent(orderManagement->{
            
            orderManagement // do something
            repository().save(orderManagement);


         });
        */

        
    }
    public static void cancelReceipt(PaymentCanceled paymentCanceled){

        /** Example 1:  new item 
        OrderManagement orderManagement = new OrderManagement();
        repository().save(orderManagement);

        */

        /** Example 2:  finding and process
        
        repository().findById(paymentCanceled.get???()).ifPresent(orderManagement->{
            
            orderManagement // do something
            repository().save(orderManagement);


         });
        */

        
    }
    public static void orderManagementStateComplete(OrderComplete orderComplete){

        /** Example 1:  new item 
        OrderManagement orderManagement = new OrderManagement();
        repository().save(orderManagement);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderComplete.get???()).ifPresent(orderManagement->{
            
            orderManagement // do something
            repository().save(orderManagement);


         });
        */

        
    }


}
