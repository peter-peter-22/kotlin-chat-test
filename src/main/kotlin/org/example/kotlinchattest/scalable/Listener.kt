package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class Listener {
    @RabbitListener(queues = [RabbitConfig.QUEUE_NAME])
    fun onMessage(message: Message){
        println("Received message from queue: $message")
    }
}