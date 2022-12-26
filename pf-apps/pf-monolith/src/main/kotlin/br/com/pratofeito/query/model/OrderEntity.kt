package br.com.pratofeito.query.model

import br.com.pratofeito.order.domain.api.model.OrderState
import java.math.BigDecimal
import javax.persistence.*

@Entity
data class OrderEntity(
    @Id val id: String,
    val aggregateVersion: Long,
    @ElementCollection val lineItems: List<OrderLineItemEmbeddable>,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") val restaurant: RestaurantEntity,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "CUSTOMER_ID") val customer: CustomerEntity,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") val courier: CourierEntity,
    val state: OrderState
)

@Embeddable
@Access(AccessType.FIELD)
data class OrderLineItemEmbeddable(
    val menuId: String,
    val name: String,
    val price: BigDecimal,
    val quantity: Int
)