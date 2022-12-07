package br.com.pratofeito.query.model

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CustomerEntity(
    @Id val id: String,
    val aggregateVersion: Long,
    val firstName: String,
    val lastName: String,
    val orderLimit: BigDecimal
)