package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class Listener(
    private val messagingTemplate: SimpMessagingTemplate
) {
    @RabbitListener(queues = [RabbitConfig.QUEUE_NAME])
    fun onMessage(message: Message) {
        println("Received message from queue: $message")
        messagingTemplate.convertAndSend("/topic/hello", message)
    }
}