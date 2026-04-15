package org.example.kotlinchattest.scalable

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

open class ReceivedMessage(
    val sender: String,
    val content: String,
    val timestamp: Long,
    type: String
) : ListenerDTO(type)

class RoomMessage(
    val room: String,
    sender: String,
    content: String,
    timestamp: Long,
    type: String
) : ReceivedMessage(sender, content, timestamp, type) {
    override fun process() {
        println("Processing message of type: $type, payload: $this, with the room message processor")
    }
}

class PrivateMessage(
    sender: String,
    content: String,
    timestamp: Long,
    type: String
) : ReceivedMessage(sender, content, timestamp, type)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RoomMessage::class, name = "room"),
    JsonSubTypes.Type(value = PrivateMessage::class, name = "private")
)
open class ListenerDTO(
    val type: String
) {
    open fun process() {
        println("Processing message of type: $type, payload: $this")
    }
}