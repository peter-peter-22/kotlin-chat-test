package org.example.kotlinchattest.scalable

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Service
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.concurrent.ConcurrentHashMap

// TODO: this bean is overall prone to race conditions, needs fixing
@Service
class UserService(
    private val messagingTemplate: SimpMessagingTemplate,
    private val ampqAdmin: AmqpAdmin,
    private val queue: Queue,
    private val exchange: TopicExchange,
    private val rabbitTemplate: RabbitTemplate
) {
    /** Get which rooms a local user is connected to. */
    private val usersToRooms = ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<String, Boolean>>()

    /** Get the local connected users of a room to. */
    private val roomsToUsers = ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<String, Boolean>>()

    private val userBindings = ConcurrentHashMap<String, Binding>()
    private val roomBindings = ConcurrentHashMap<String, Binding>()

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
            return true
        } else {
            println("User $user already bound")
            return false
        }
    }

    private fun unregisterUserSession(user: String) {
        println("Unregistering user $user")
        val userBinding = userBindings[user]
        if (userBinding != null) {
            ampqAdmin.removeBinding(userBinding)
            userBindings.remove(user)
        }
        for (room in usersToRooms[user] ?: emptySet()) {
            leaveRoom(user, room)
        }
    }

    private fun bindToUser(userId: String) {
        println("Binding to user $userId")
        val routingKey = "user.$userId"
        val binding = BindingBuilder.bind(queue)
            .to(exchange)
            .with(routingKey)
        ampqAdmin.declareBinding(binding)
        userBindings[userId] = binding
    }

    fun receiveFromUser(userId: String, payload: PrivateMessage) {
        println("Sending private message to user $userId")
        messagingTemplate.convertAndSendToUser(userId, "/topic/private", payload)
    }

    fun sendToUser(fromUser: String, toUser: String, message: String) {
        val routingKey = "user.$toUser"
        val event = PrivateMessage(
            content = message,
            sender = fromUser,
            timestamp = System.currentTimeMillis(),
            receiver = toUser,
        )
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, routingKey, event)
    }

    fun joinRoom(userId: String, roomId: String) {
        println("Joining room $roomId for user $userId")
        usersToRooms.computeIfAbsent(userId) { ConcurrentHashMap.newKeySet() }.add(roomId)
        roomsToUsers.computeIfAbsent(roomId) { ConcurrentHashMap.newKeySet() }.add(userId)

        if (!roomBindings.containsKey(roomId)) {
            println("Binding room $roomId")
            val routingKey = "room.$roomId"
            val binding = BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
            ampqAdmin.declareBinding(binding)
            roomBindings[roomId] = binding
        }
    }

    fun leaveRoom(userId: String, roomId: String) {
        println("Leaving room $roomId for user $userId")
        usersToRooms[userId]?.remove(roomId)
        roomsToUsers[roomId]?.remove(userId)

        if (roomsToUsers[roomId]?.size == 0) {
            if (roomBindings.containsKey(roomId)) {
                println("Removing binding of room $roomId")
                val roomBinding = roomBindings[roomId]
                if (roomBinding != null) {
                    ampqAdmin.removeBinding(roomBinding)
                    roomBindings.remove(roomId)
                }
            } else {
                println("Binding of room $roomId does not exists")
            }
        }
    }

    fun sendToRoom(roomId: String, fromUser: String, message: String) {
        println("Sending room message to room $roomId")
        val routingKey = "room.$roomId"
        val event = RoomMessage(
            content = message,
            sender = fromUser,
            timestamp = System.currentTimeMillis(),
            room = roomId
        )
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, routingKey, event)
    }

    fun receiveFromRoom(roomId: String, payload: RoomMessage) {
        println("Received room message from room $roomId")
        messagingTemplate.convertAndSend("/topic/room.$roomId", payload)
    }
}