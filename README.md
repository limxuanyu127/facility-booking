# facility-booking

## To compile

`cd src/main/java` \
`javac client/Main.java` \
`javac server/Server.java`

## To start a server

`cd src/main/java` \
`java server.Server [PORT_NUMBER] [AT_MOST_ONCE] [PACKET_DROP_RATE]`

- PORT_NUMBER: server port number
- AT_MOST_ONCE: boolean, true to start server using at-most-once semantics and false to start using at-least-once semantics
- PACKET_DROP_RATE: probability that a packet will be dropped by the server

## To start a client
`cd src/main/java` \
`java client.Main [HOST_NAME] [SERVER_PORT] [CLIENT_PORT] [PACKET_DROP_RATE] [TIMEOUT] [MAX_TRIES]`

- HOST_NAME: IP address of server
- SERVER_PORT: server port number
- CLIENT_PORT: client port number
- PACKET_DROP_RATE: probability that a packet will be dropped by the client
- TIMEOUT: client socket timeout in milliseconds
- MAX_TRIES: maximum number of retries




