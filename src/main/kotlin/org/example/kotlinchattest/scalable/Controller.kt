package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@ConditionalOnProperty(name=["common.mode"], havingValue="scalable")
@Controller
class Controller(
    private val rabbitTemplate: RabbitTemplate
) {
    @MessageMapping("/hello")
    fun greeting(message: Message): String {
        println("Received message from client: $message")
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "chat", message)
        return "hello"
    }
}