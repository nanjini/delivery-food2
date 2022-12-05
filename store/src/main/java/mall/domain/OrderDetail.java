package mall.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;


@Entity
@Table(name="OrderDetail_table")
@Data
public class OrderDetail {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


}
