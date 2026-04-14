package org.example.kotlinchattest.scalable

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@ConditionalOnProperty(name=["common.mode"], havingValue="scalable")
@Configuration
@EnableWebSocketMessageBroker
class ConfigureWebsocket: WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Prefix of outgoing message endpoints
        config.enableSimpleBroker("/topic")
        // Prefix of incoming message endpoints
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/stomp")
    }
}