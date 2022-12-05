package mall.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;


@Entity
@Table(name="DeliveryState_table")
@Data
public class DeliveryState {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


}
