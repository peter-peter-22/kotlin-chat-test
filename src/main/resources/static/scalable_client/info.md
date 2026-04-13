### Minimal websocket example.
#### Includes:
- A websocket endpoint that replies with a greeting.
- A client that connects to the endpoint and prints the greeting.
- Stomp format usage.
- RabbitMQ scalable message broker with STOMP plugin.

#### Architecture:
There are two STOMP communications, one between the client and the server, another between he server and rabbitmq.

The server forms its own stomp messages, not just forwards.

The stomp format between the client and the server is optional and doesn't affects the other stomp channel.