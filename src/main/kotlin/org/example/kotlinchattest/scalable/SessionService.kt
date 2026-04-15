package org.example.kotlinchattest.scalable

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Service
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService(
    private val messagingTemplate: SimpMessagingTemplate,
    private val ampqAdmin: AmqpAdmin,
    private val queue: Queue,
    private val exchange: TopicExchange
) {
    /** Get which rooms a local user is connected to. */
    private val usersToRooms = ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<String, Boolean>>()
    /** Get the local connected users of a room to. */
    private val roomsToUsers = ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<String, Boolean>>()

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val user = headerAccessor.user?.name
            ?: throw IllegalArgumentException("Missing user in principal") // TODO: disconnect on error

        if (registerUserSession(user)) {
            println("User $user connected")
            // The existing room connections could be loaded from the database here
        }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val user = headerAccessor.user?.name ?: "anonymous"
        println("User $user disconnected")
        unregisterUserSession(user)
    }

    private fun registerUserSession(user: String): Boolean {
        if (!usersToRooms.containsKey(user)) {
            bindToUser(user)
            usersToRooms[user] = ConcurrentHashMap.newKeySet()
            return true
        } else {
            println("User $user already bound")
            return false
        }
    }

    private fun unregisterUserSession(user: String) {
        usersToRooms.remove(user)
    }

    private fun bindToUser(userId: String) {
        println("Binding to user $userId")
        val routingKey = "user.$userId"
        val binding = BindingBuilder.bind(queue)
            .to(exchange)
            .with(routingKey)
        ampqAdmin.declareBinding(binding)
    }

    fun sendToUser(userId: String, payload: PrivateMessage) {
        println("Sending private message to user $userId")
        messagingTemplate.convertAndSendToUser(userId, "/topic/private", payload)
    }
}