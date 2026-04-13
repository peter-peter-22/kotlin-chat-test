package org.example.kotlinchattest.scalable

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@ConditionalOnProperty(name=["common.mode"], havingValue="scalable")
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    @Value($$"${stomp.broker.relay.host:localhost}")
    private val relayHost: String = "localhost"

    @Value($$"${stomp.broker.relay.port:61613}")
    private val relayPort = 0

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app")
        registry.setUserDestinationPrefix("/user")

        // RabbitMQ as STOMP broker (scalable)
        registry.enableStompBrokerRelay("/topic/", "/queue/")
            .setRelayHost(relayHost)
            .setRelayPort(relayPort)
            .setClientLogin("guest")
            .setClientPasscode("guest")
            .setSystemLogin("guest")
            .setSystemPasscode("guest")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // no security
    }
}