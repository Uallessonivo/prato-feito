package br.com.pratofeito.query.handler

import br.com.pratofeito.order.domain.api.*
import br.com.pratofeito.order.domain.api.model.OrderState
import br.com.pratofeito.query.model.OrderEntity
import br.com.pratofeito.query.model.OrderItemEmbeddable
import br.com.pratofeito.query.repository.CourierRepository
import br.com.pratofeito.query.repository.CustomerRepository
import br.com.pratofeito.query.repository.OrderRepository
import br.com.pratofeito.query.repository.RestaurantRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("order")
class OrderHandler(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val restaurantRepository: RestaurantRepository,
    private val courierRepository: CourierRepository,
    private val messagingTemplate: SimpMessageSendingOperations
) {

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderCreationInitiatedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderItems = ArrayList<OrderItemEmbeddable>()
        for (item in event.orderDetails.lineItems) {
            val orderItem = OrderItemEmbeddable(
                item.menuItemId,
                item.name,
                item.price.amount,
                item.quantity
            )
            orderItems.add(orderItem)
        }
        orderRepository.save(
            OrderEntity(
                event.aggregateIdentifier.identifier,
                aggregateVersion,
                orderItems,
                null,
                null,
                null,
                OrderState.CREATE_PENDING
            )
        )
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderVerifiedByCustomerEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        val customerEntity = customerRepository.findById(event.customerId.identifier).get()

        orderEntity.customer = customerEntity
        orderEntity.state = OrderState.VERIFIED_BY_CUSTOMER
        orderEntity.aggregateVersion = aggregateVersion
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderVerifiedByRestaurantEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        val restaurantEntity = restaurantRepository.findById(event.restaurantId.identifier).get()

        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.restaurant = restaurantEntity
        orderEntity.state = OrderState.VERIFIED_BY_RESTAURANT
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.PREPARED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderReadyForDeliveryEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.READY_BY_DELIVERY
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderDeliveredEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.DELIVERED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: OrderRejectedEvent, @SequenceNumber aggregateVersion: Long) {
        val orderEntity = orderRepository.findById(event.aggregateIdentifier.identifier).get()
        orderEntity.aggregateVersion = aggregateVersion
        orderEntity.state = OrderState.REJECTED
        orderRepository.save(orderEntity)
        broadcastUpdates()
    }

    @ResetHandler
    fun onReset() = orderRepository.deleteAll()

    private fun broadcastUpdates() =
        messagingTemplate.convertAndSend("/topic/orders.updates", orderRepository.findAll())
}