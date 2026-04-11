### STOMP syntax
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
* Chat group affinity is not needed for small to medium groups, might be necessary for large groups
* Is the default round-robin load balancer good enough?

### Links
* [spring AMQP introduction](https://docs.spring.io/spring-boot/reference/messaging/amqp.html)
* [spring rabbit MQ](https://spring.io/guides/gs/messaging-rabbitmq)
* [stomp and spring boot websocker tutorial](https://www.dariawan.com/tutorials/spring/spring-boot-websocket-stomp-tutorial/)
* [spring websocket stomp](https://spring.io/guides/gs/messaging-stomp-websocket)