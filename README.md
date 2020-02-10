
## Tiny HTTP Server

A tiny HTTP server implementation, for fun and learning. It's not not fully HTTP compliant, but can handle simple GET requests, and possibly more in the future. I built it as a little side project after reading a couple of chapters from Elliotte Harold's [Java Network Programming](http://shop.oreilly.com/product/0636920028420.do)


### Overview
Here's a quick overview of the architecture.
```
 .-------------------------------------------------------------------------------.
 |                          Tiny HTTP Server                                     |
 | .-----------------.  .------------------------.   .------------------------.  |
 | | SocketServer    |  |      Executor Pool     |   |     SocketProcessor    |  |
 | |                 |  |                        |   |   socket -> [Request]  |  |
 | | SocketProcessor ----> [ SocketProcessor ]   |   |   socket -> [Response] |  |
 | |      ^          |  |   [ SocketProcessor ] ------>               |       |  | 
 | |      |          |  .------------------------.   |                |       |  |
 | |    socket       |                               |                v       |  |
 | |      |          |  .------------------------.   |  [ RequestHandler ]    |  |
 | | waits incoming  |  |    RequestHandler    <-------  [ RequestHandler ]   |  |
 | |  connection     |  |                        |   .------------------------.  | 
 | |      ^          |  |    Request / Response  |                               |
 | .------|----------.  .-----------|------------.                               |
 .--------|-------------------------|--------------------------------------------.        
          |                         |
          ^                         V
      HTTP request              HTTP response     
```
- upon start up the server creates a `SocketServer`, binds to the local host and given port, and awaits for incoming connections
- each incoming connection (as a Socket) is given to a `SocketProcessor` and added to a local `Executor` pool (i.e, thread pool)
- the `Executor` service eventually calls on the `SocketProcessor` to handle the `Socket`
- the `SocketProcessor` builds the underlying `Request` and `Response` for the given Socket
connection, and cycles through a list `RequestHandlers`, giving each a chance to handle
the `Request`/`Response`
- the `RequestHandler` sends the response back to client after completing it's request

### Running the server

Running with no options, the server will use the following defaults: `port=8000`, `pool-size=10`, `document-root=.`

```bash
$ mvn compile
$ java -cp target/classes com.gnoht.ths.Server
```

or with options.

```bash
$ java -cp target/classes com.gnoht.ths.Server \
  --port=8080
  --document-root=/my/document/root
  --pool-size=50
```

