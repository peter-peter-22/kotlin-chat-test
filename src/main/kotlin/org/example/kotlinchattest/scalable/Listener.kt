package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Listener(
    messageProcessors: List<MessageProcessor<*>> 
){
    @Suppress("UNCHECKED_CAST")
    val processors = messageProcessors.associateBy { it.type } as Map<MessageType, MessageProcessor<ListenerDTO>>
    @RabbitListener(queues = ["#{chatQueue.name}"]) // Resolves to the only queue name in @RabbitListener
    fun onMessage(message: ListenerDTO) {
        println("Received message from queue: $message")
        val processor = processors[message.type]
        processor?.process(message) ?: throw IllegalArgumentException("No processor found for message type: ${message.type}")
    }
}