package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class Listener(
    private val messagingTemplate: SimpMessagingTemplate,
    private val rabbitConfig: RabbitConfig
) {
    @RabbitListener(queues = ["#{chatQueue.name}"]) // Resolves to the only queue name in @RabbitListener
    fun onMessage(message: ListenerDTO) {
        println("Received message from queue: $message")
        message.process()
        messagingTemplate.convertAndSend("/topic/hello", message)
    }
}