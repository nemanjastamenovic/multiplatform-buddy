package com.myapp.data.local

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject


class MyServer @Inject constructor() {
    private lateinit var server: NettyApplicationEngine

    @Serializable
    data class Response(val status: String)

    @Serializable
    data class Request(
        val id: String,
        val quantity: Int,
        val isTrue: Boolean
    )

    fun startServer(): Boolean {
        server = embeddedServer(Netty, port = 8080) {
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
                get("/") {
                    call.respond(Response(status = "OK"))
                    //call.respondText("Hello World")
                }
                post("/") {
                    val request = call.receive<Request>()
                    call.respond(request)
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
}