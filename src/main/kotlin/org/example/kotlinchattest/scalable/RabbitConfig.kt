package org.example.kotlinchattest.scalable

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.support.converter.MessageConverter
import java.util.UUID

@Configuration
class RabbitConfig {
    private val queueName = UUID.randomUUID().toString()
    private val exchangeName = "chat"

    @Bean
    fun chatExchange(): TopicExchange {
        return TopicExchange(exchangeName, true, false) // durable, not auto-delete
    }

    @Bean
    fun chatQueue(): Queue? {
        return Queue(queueName, false, false, true) // not durable, not exclusive, auto-delete
    }

    @Bean
    fun chatBinding(chatQueue: Queue, chatExchange: TopicExchange): Binding? {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with("#") // listen to every message
    }

    @Bean
    fun messageConverter():MessageConverter = JacksonJsonMessageConverter() // Enable the transfer of JSON messages

    companion object{
        const val QUEUE_PLACEHOLDER="#{chatQueue.name}" // Resolves to the only queue name
    }
}