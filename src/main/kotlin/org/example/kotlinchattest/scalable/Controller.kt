package org.example.kotlinchattest.scalable

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

data class SendPrivateMessageDTO(
    val receiver: String,
    val content: String
)

data class SendRoomMessageDTO(
    val room: String,
    val content: String
)

data class RoomJoinDTO(
    val room: String
)

@ConditionalOnProperty(name = ["common.mode"], havingValue = "scalable")
@Controller
class Controller(
    private val userService: UserService
) {
    @MessageMapping("/private")
    fun private(body: SendPrivateMessageDTO, principal: Principal) {
        println("Received private message from client: $body")
        userService.sendToUser(principal.name, body.receiver, body.content)
    }

    @MessageMapping("/room")
    fun room(body: SendRoomMessageDTO, principal: Principal) {
        println("Received room message from client: $body")
        userService.sendToRoom(body.room, principal.name, principal.name)
    }

    @MessageMapping("/joinRoom")
    fun joinRoom(body: RoomJoinDTO, principal: Principal) {
        println("Received room message from client: $body")
        userService.joinRoom(principal.name, body.room)
    }

    @MessageMapping("/leaveRoom")
    fun leaveRoom(body: RoomJoinDTO, principal: Principal) {
        println("Received room message from client: $body")
        userService.leaveRoom(principal.name, body.room)
    }
}