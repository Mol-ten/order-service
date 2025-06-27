package kz.molten.techshop.orderservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "order_service", name = "customer_delivery_info")
public class CustomerDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_delivery_info_id_gen")
    @SequenceGenerator(name = "customer_delivery_info_id_gen", sequenceName = "customer_delivery_info_id_seq")
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "address")
    private String address;

    @NotNull
    @Min(1)
    @Column(name = "customer_user_id")
    private Long customerUserId;

    @OneToMany(mappedBy = "customerDeliveryInfo")
    @ToString.Exclude
    @JsonIgnore
    private List<Order> order;
}
