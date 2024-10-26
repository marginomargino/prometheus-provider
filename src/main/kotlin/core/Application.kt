package core

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import io.prometheus.metrics.core.metrics.Gauge
import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter
import io.prometheus.metrics.model.registry.PrometheusRegistry
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPOutputStream

fun main() {

    val dotenv = dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    val accessToken = dotenv["ACCESS_TOKEN"] ?: error("ACCESS_TOKEN not set in .env")

    val humidityMetric = Gauge.builder()
        .name("humidity")
        .help("Humidity %")
        .register()

    val temperatureMetric = Gauge.builder()
        .name("temperature_c")
        .help("Temperature in Celsius")
        .register()


    embeddedServer(Netty, port = 8080) {
        install(createRouteScopedPlugin("BearerAuthPlugin") {
            onCall { call ->
                val authHeader = call.request.headers["Authorization"]

                if (authHeader == null
                    || !authHeader.startsWith("Bearer ")
                    || authHeader.removePrefix("Bearer ") != accessToken
                ) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@onCall
                }
            }
        })

        install(ContentNegotiation) {
            json()
        }
        routing {
            get("metrics") {
                val writer = PrometheusTextFormatWriter(false)
                val snapshots = PrometheusRegistry.defaultRegistry.scrape()

                val acceptEncoding = call.request.headers["Accept-Encoding"] ?: ""

                val output = when {
                    acceptEncoding.contains("gzip", ignoreCase = true) ->
                        ByteArrayOutputStream().use { byteStream ->
                            GZIPOutputStream(byteStream).use { gzipStream ->
                                writer.write(gzipStream, snapshots)
                            }
                            byteStream.toByteArray()
                        }

                    else -> ByteArrayOutputStream().use { byteStream ->
                        writer.write(byteStream, snapshots)
                        byteStream.toByteArray()
                    }
                }

                call.respond(object : OutgoingContent.WriteChannelContent() {
                    override val contentType: ContentType = ContentType.Text.Plain.withCharset(Charsets.UTF_8)

                    override suspend fun writeTo(channel: ByteWriteChannel) {
                        channel.writeFully(ByteBuffer.wrap(output))
                    }
                })
            }

            post("/sensors") {
                val sensorData = call.receive<SensorData>()

                humidityMetric.set(sensorData.humidity)
                temperatureMetric.set(sensorData.temperature)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }.start(wait = true)
}
