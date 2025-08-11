package org.aryak.batch.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Order {

    private Long orderId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal cost;
    private String itemId;
    private String itemName;
    private String shipDate;

}
