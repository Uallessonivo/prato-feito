package br.com.pratofeito.restaurant.domain

import br.com.pratofeito.restaurant.domain.api.CreateRestaurantOrderCommand
import br.com.pratofeito.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import br.com.pratofeito.restaurant.domain.api.RestaurantOrderCreatedEvent
import br.com.pratofeito.restaurant.domain.api.RestaurantOrderPreparedEvent
import br.com.pratofeito.restaurant.domain.api.model.RestaurantId
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderId
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderLineItem
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderState
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle

internal class RestaurantOrder {
    private lateinit var id: RestaurantOrderId
    private lateinit var restaurantId: RestaurantId
    private lateinit var state: RestaurantOrderState
    private lateinit var lineItems: List<RestaurantOrderLineItem>
    constructor()

    constructor(command: CreateRestaurantOrderCommand) {
        AggregateLifecycle.apply(
            RestaurantOrderCreatedEvent(
                command.orderDetails.lineItems,
                command.restaurantOrderId,
                command.targetAggregateIdentifier,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        id = event.restaurantOrderId
        restaurantId = event.aggregateIdentifier
        lineItems = event.lineItems
        state = RestaurantOrderState.CREATED
    }

    @CommandHandler
    fun handle(command: MarkRestaurantOrderAsPreparedCommand) {
        if (RestaurantOrderState.CREATED == state) {
            AggregateLifecycle.apply(
                RestaurantOrderPreparedEvent(
                    command.targetAggregateIdentifier,
                    command.auditEntry
                )
            )
        } else {
            throw UnsupportedOperationException("O estado n??o ?? CREATED")
        }
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        state = RestaurantOrderState.PREPARED
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}