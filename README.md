# clojure-rest-api

Clojure REST Api based on (this post)[https://blog.interlinked.org/programming/clojure_rest.html]

## Routes:
GET /api/v1/todos
POST /api/v1/todos
GET /api/v1/todos/:id
PUT /api/v1/todos/:id
DELETE /api/v1/todos/:id

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 MIT
