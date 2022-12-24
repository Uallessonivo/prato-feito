package br.com.pratofeito.query.model

import br.com.pratofeito.restaurant.domain.api.model.RestaurantId
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderState
import javax.persistence.*

@Entity
class RestaurantOrderEntity(
    @Id var id: String,
    var aggregateVersion: Long,
    var lineItems: List<RestaurantOrderItemEmbeddable>,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "RESTAURANT_ID") var restaurant: RestaurantEntity,
    @Enumerated var state: RestaurantOrderState
)

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantOrderItemEmbeddable(
    var menuId: String,
    var name: String,
    var quantity: Int
)