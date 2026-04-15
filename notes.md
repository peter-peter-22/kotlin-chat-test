### RabbitMQ
The messages are routed inside the exchange, 
which then routes them to the appropriate queue(s) 
based on the binding keys.

- Exchange: A messaging channel, similar to topic.
- Producer: A process that sends messages to an exchange. The messages have a routing key.
- Queue: A storage for messages. Can be bound to routing key patterns, receives messages from the exchange those match the routing key patterns.
- Binding: A relationship between an exchange and a queue, defined by a binding key pattern. Decides what messages a queue gets.
- Consumer: A process that receives messages from a queue.

#### Example scenario
- #### Intro
- Defined exchange: messages
- The routing keys of the messages in group 1 are prefixed with "group.1"
- The routing keys of the direct messages of user x are prefixed with "user.x" 

- #### Events
- The instance connects to the exchange "messages" and creates a queue
- User 1 joins the chat
- Binding is created to "user.1" 
- -> now the user can receive direct messages
- The user opens group 1
- Binding is created to "group.1" if not already exists (one queue per instance) 
- -> now the user can see the messages of the group
- The user disconnects
- The binding of "user.1" is removed, "group.1" is removed only if no other online user is in the group

### The app
The app can operate in two modes, basic (single instance) and scalable (rabbitmq).

The mode can be set in application.yaml

### STOMP

A standardized format for endpoints and actions in websocket.

Syntax:
```
COMMAND
header1:value1
header2:value2

bodyNUL
```
Where NUL is "&#0;" aka. "\u0000" (most text editors can't use it)

This is the intended format of STOMP messages, and it's mentioned in the documentation.

The body is optional.

### Production
* Fix the race conditions of the UserService bean
* Limited to medium size, a larger size requires some sharding mechanism.
* Is the default round-robin load balancer good enough?
* Inter-cluster communication requires manual setup with the "shovel" mechanism.

### Links
* [spring AMQP java (and tutorials)](https://docs.spring.io/spring-amqp/reference/introduction/quick-tour.html)
* [spring AMQP by spring](https://docs.spring.io/spring-boot/reference/messaging/amqp.html)
* [spring AMQP by rabbit](https://www.rabbitmq.com/tutorials/tutorial-one-spring-amqp)
* [spring rabbit MQ introduction](https://spring.io/guides/gs/messaging-rabbitmq)
* [stomp and spring boot websocket tutorial](https://www.dariawan.com/tutorials/spring/spring-boot-websocket-stomp-tutorial/)
* [spring websocket stomp](https://spring.io/guides/gs/messaging-stomp-websocket)
* [rabbit MQ docker](https://hub.docker.com/_/rabbitmq)
* [rabbit MQ stomp](https://www.rabbitmq.com/docs/stomp)
* [rabbit MQ tutorial](https://www.rabbitmq.com/tutorials)
* [rabbit MQ web stomp](https://www.rabbitmq.com/docs/web-stomp)
* [rabbit MQ java client](https://github.com/rabbitmq/rabbitmq-tutorials/tree/main/java-gradle)