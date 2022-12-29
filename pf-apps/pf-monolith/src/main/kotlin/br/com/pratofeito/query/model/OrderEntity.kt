package br.com.pratofeito.query.model

import br.com.pratofeito.order.domain.api.model.OrderState
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.math.BigDecimal
import javax.persistence.*

@Entity
data class OrderEntity(
    @Id val id: String,
    var aggregateVersion: Long,
    @ElementCollection val lineItems: List<OrderItemEmbeddable>,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") var restaurant: RestaurantEntity?,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "CUSTOMER_ID") var customer: CustomerEntity?,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") val courier: CourierEntity?,
    var state: OrderState
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}

@Embeddable
@Access(AccessType.FIELD)
data class OrderItemEmbeddable(
    var menuId: String,
    var name: String,
    var price: BigDecimal,
    var quantity: Int
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}