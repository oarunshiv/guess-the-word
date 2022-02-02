# guess-the-word
This is a word game inspired by Wordle. It contains a server which generates a word and assess the client's response. The Client sends request and tries to identify the word with least amount of turns. Both server and client are based on grpc-kotlin.


## protos
This folder contains the proto definition of the service, request and response objects. 

## server
This folder contains the server code which authenticates client requests, assigns a random 5-letter word to each user and then evaluates every clients request.
To run the server, execute the following command:  
`./gradlew :server:run` - This will start the server at port 50051.   
`./gradlew :server:run --args="-p <portnumber>"` - Start the server on `<portnumber>`.

## client
This folder contains the client code which generates a 5-letter word to guess and sends the request to server.

Implement the `WordGuessser` interface and update Main.Kt within client folder to specify your implementation.
If the class is implemented in java, add it within `client > main > java`, else to implement in Kotlin add it to `client > main > kotlin.`
To run the client, execute the following command:
`./gradlew :client:run` - This will start the server at port 50051.   
`./gradlew :client:run --args="-p <portnumber>"` - Start the server on `<portnumber>`.