package org.example.kotlinchattest.scalable

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

data class SendPrivateMessageDTO(
    val receiver:String,
    val content:String
)

@ConditionalOnProperty(name=["common.mode"], havingValue="scalable")
@Controller
class Controller(
    private val rabbitTemplate: RabbitTemplate
) {
    @MessageMapping("/private")
    fun greeting(body: SendPrivateMessageDTO, principal: Principal) {
        println("Received private message from client: $body")
        val event=PrivateMessage(
            content = body.content,
            sender = principal.name,
            timestamp = System.currentTimeMillis(),
            receiver = body.receiver,
        )
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "chat", event)
    }
}