package br.com.pratofeito.query.model

import br.com.pratofeito.restaurant.domain.api.model.RestaurantId
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderState
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import javax.persistence.*

@Entity
class RestaurantOrderEntity(
    @Id var id: String,
    var aggregateVersion: Long,
    @ElementCollection var lineItems: List<RestaurantOrderItemEmbeddable>,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") var restaurant: RestaurantEntity,
    @Enumerated var state: RestaurantOrderState
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}


@Embeddable
@Access(AccessType.FIELD)
data class RestaurantOrderItemEmbeddable(
    var menuId: String,
    var name: String,
    var quantity: Int
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}