package org.example.kotlinchattest.scalable

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

enum class MessageType {
    ROOM, PRIVATE
}

open class ReceivedMessage(
    val sender: String,
    val content: String,
    val timestamp: Long,
    type: MessageType
) : ListenerDTO(type)

class RoomMessage(
    val room: String,
    sender: String,
    content: String,
    timestamp: Long,
    type: MessageType=MessageType.ROOM
) : ReceivedMessage(sender, content, timestamp, type)

class PrivateMessage(
    val receiver: String,
    sender: String,
    content: String,
    timestamp: Long,
    type: MessageType=MessageType.PRIVATE
) : ReceivedMessage(sender, content, timestamp, type)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RoomMessage::class, name = "ROOM"),
    JsonSubTypes.Type(value = PrivateMessage::class, name = "PRIVATE")
)
open class ListenerDTO(
    val type: MessageType
)