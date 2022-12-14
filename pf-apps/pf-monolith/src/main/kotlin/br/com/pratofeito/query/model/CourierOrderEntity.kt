package br.com.pratofeito.query.model

import br.com.pratofeito.courier.domain.api.model.CourierOrderState
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import javax.persistence.*

@Entity
data class CourierOrderEntity(
    @Id val id: String,
    val aggregateVersion: Long,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "COURIER_ID") var courier: CourierEntity?,
    @Enumerated(EnumType.STRING) var state: CourierOrderState
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}