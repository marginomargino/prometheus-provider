package core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SensorData(
    val humidity: Double,
    @SerialName("temperature_c")
    val temperature: Double,
)
