package org.example.kotlinchattest.scalable

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@ConditionalOnProperty(name=["common.mode"], havingValue="scalable")
@Controller
class ChatController(private val messagingTemplate: SimpMessagingTemplate) {
    // CHAT ROOMS (pub/sub via /topic/room/...)
    @MessageMapping("/send/room/{roomId}")
    fun sendToRoom(
        @DestinationVariable roomId: String?,
        @Payload message: ChatMessage
    ) {
        println("Sending message to room: $roomId")
        messagingTemplate.convertAndSend("/topic/room/$roomId", message)
    }

    // DIRECT MESSAGES (routed to personal topic)
    // These are not direct messages between 2 users
    @MessageMapping("/send/direct/{toUser}")
    fun sendDirect(
        @DestinationVariable toUser: String?,
        @Payload message: ChatMessage
    ) {
        println("Sending message to user: $toUser")
        messagingTemplate.convertAndSend("/topic/user/$toUser", message)
    }
}