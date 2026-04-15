package org.example.kotlinchattest.scalable

import org.springframework.stereotype.Component

interface MessageProcessor<in T: ListenerDTO> {
    val type: MessageType
    fun process(message: T)
}

@Component
class PrivateMessageProcessor(
    private val sessionService: SessionService
) : MessageProcessor<PrivateMessage> {
    override val type = MessageType.PRIVATE
    override fun process(message: PrivateMessage) {
        sessionService.sendToUser(message.receiver, message)
    }
}

@Component
class RoomMessageProcessor(
    private val sessionService: SessionService
) : MessageProcessor<RoomMessage> {
    override val type = MessageType.ROOM
    override fun process(message: RoomMessage) {
        print("hello room")
    }
}