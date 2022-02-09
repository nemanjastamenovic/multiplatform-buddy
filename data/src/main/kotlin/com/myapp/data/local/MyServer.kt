package com.myapp.data.local

import com.myapp.data.model.ActionItem
import com.myapp.data.model.FeatureItem
import com.myapp.data.repo.MyRepo
import com.toxicbakery.logging.Arbor
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import java.net.InetAddress
import java.net.UnknownHostException
import java.time.Duration
import javax.inject.Inject

const val REST_ENDPOINT_FEATURE = "/feature"
const val REST_ENDPOINT_ACTION = "/action"

class MyServer @Inject constructor(private val myRepo: MyRepo) {
    private lateinit var server: NettyApplicationEngine
    fun startServer(): Boolean {
        server = embeddedServer(Netty, port = 8080) {
            install(DefaultHeaders)
            install(CORS) {
                maxAge = Duration.ofDays(1)
            }
            install(StatusPages) {
                exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    json()
                })
            }
            routing {

                //main route
                get("/") {
                    call.respondHtml {
                        head {
                            title("KM Buddy Application")
                        }
                        body {
                            h1 { +"Hello KM Engineers!" }
                            p {
                                +"How are you doing?"
                            }
                        }
                    }
                }

                //feature routes
                get("$REST_ENDPOINT_FEATURE/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                        Arbor.d("Get entity with Id=$id")
                        call.respond(myRepo.getFeature(id))
                    }
                }
                get(REST_ENDPOINT_FEATURE) {
                    errorAware {
                        Arbor.d("Get all entities")
                        call.respond(myRepo.getAllFeatures())
                    }
                }
                post(REST_ENDPOINT_FEATURE) {
                    errorAware {
                        val receive = call.receive<FeatureItem>()
                        println("Received Post Request: $receive")
                        call.respond(myRepo.addFeature(receive))
                    }
                }

                //action routes
                get("$REST_ENDPOINT_ACTION/{id}") {
                    errorAware {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                        Arbor.d("Get entity with Id=$id")
                        call.respond(myRepo.getAction(id))
                    }
                }
                get(REST_ENDPOINT_ACTION) {
                    errorAware {
                        Arbor.d("Get all entities")
                        call.respond(myRepo.getAllActions())
                    }
                }
                post(REST_ENDPOINT_ACTION) {
                    errorAware {
                        val receive = call.receive<ActionItem>()
                        println("Received Post Request: $receive")
                        call.respond(myRepo.addAction(receive))
                    }
                }
            }
        }
        try {
            server.start(wait = false)
        } catch (e: Exception) {
            println(e.localizedMessage)
            return false
        }
        return true
    }

    fun stopServer(): Boolean {
        try {
            server.stop(gracePeriodMillis = 500L, timeoutMillis = 500L)
        } catch (e: Exception) {
            println(e.localizedMessage)
            return false
        }
        return true
    }

    fun findMyIp(): String {
        val ip: InetAddress
        val hostname: String
        return try {
            ip = InetAddress.getLocalHost()
            hostname = ip.hostName
            println("Your current IP address : $ip")
            println("Your current Hostname : $hostname")
            ip.toString()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ""
        }
    }

    private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
        return try {
            block()
        } catch (e: Exception) {
            call.respondText(
                """{"error":"$e"}""",
                ContentType.parse("application/json"),
                HttpStatusCode.InternalServerError
            )
            null
        }
    }

    private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true) = respond("""{"success": "$value"}""")
}