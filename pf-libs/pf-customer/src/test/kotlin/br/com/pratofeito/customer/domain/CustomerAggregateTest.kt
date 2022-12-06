package br.com.pratofeito.customer.domain

import br.com.pratofeito.common.domain.api.model.AuditEntry
import br.com.pratofeito.common.domain.api.model.Money
import br.com.pratofeito.common.domain.api.model.PersonName
import br.com.pratofeito.customer.domain.api.CreateCustomerCommand
import br.com.pratofeito.customer.domain.api.CreateCustomerOrderCommand
import br.com.pratofeito.customer.domain.api.CustomerCreatedEvent
import br.com.pratofeito.customer.domain.api.CustomerOrderCreatedEvent
import br.com.pratofeito.customer.domain.api.model.CustomerId
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.Calendar

class CustomerAggregateTest {
    private lateinit var fixture: FixtureConfiguration<Customer>
    private val who = "uallesson"
    private val auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)
    private val auditEntry2: AuditEntry = AuditEntry(who + "2", Calendar.getInstance().time)
    private var orderLimit = Money(BigDecimal.valueOf(10000000))

    @Before
    fun setup() {
        fixture = AggregateTestFixture(Customer::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    fun createCustomerTest() {
        val name = PersonName("Uallesson", "Nunes")
        val createCustomerCommand = CreateCustomerCommand(name, orderLimit, auditEntry)
        val customerCreatedEvent =
            CustomerCreatedEvent(name, orderLimit, createCustomerCommand.targetAggregateIdentifier, auditEntry)

        fixture.given()
            .`when`(createCustomerCommand)
            .expectEvents(customerCreatedEvent)
    }

    @Test
    fun createCustomerOrderTest() {
        val name = PersonName("Uallesson", "Nunes")
        val createCustomerOrderCommand = CreateCustomerOrderCommand(CustomerId("customerId"), orderLimit, auditEntry)
        val customerCreatedEvent =
            CustomerCreatedEvent(
                name,
                orderLimit.add(Money(BigDecimal.ONE)),
                createCustomerOrderCommand.targetAggregateIdentifier,
                auditEntry2
            )
        val customerOrderCreatedEvent = CustomerOrderCreatedEvent(
            orderLimit,
            CustomerId("customerId"),
            createCustomerOrderCommand.customerOrderId,
            auditEntry
        )

        fixture.given(customerCreatedEvent)
            .`when`(createCustomerOrderCommand)
            .expectEvents(customerOrderCreatedEvent)
    }

    @Test
    fun createCustomerOrderFailOrderLimitTest() {
        val name = PersonName("Uallesson", "Nunes")
        val createCustomerOrderCommand = CreateCustomerOrderCommand(CustomerId("customerId"), orderLimit, auditEntry)
        val customerCreatedEvent =
            CustomerCreatedEvent(name, orderLimit, createCustomerOrderCommand.targetAggregateIdentifier, auditEntry)
        val customerOrderCreatedEvent = CustomerOrderCreatedEvent(
            orderLimit,
            CustomerId("customerId"),
            createCustomerOrderCommand.customerOrderId,
            auditEntry
        )

        fixture.given(customerCreatedEvent)
            .`when`(createCustomerOrderCommand)
            .expectException(UnsupportedOperationException::class.java)
    }
}

