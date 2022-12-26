package br.com.pratofeito.query.model

import br.com.pratofeito.courier.domain.api.model.CourierOrderState
import javax.persistence.*

@Entity
data class CourierOrderEntity(
    @Id val id: String,
    val aggregateVersion: Long,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") val courier: CourierEntity,
    @Enumerated(EnumType.STRING) val state: CourierOrderState
)