(ns clojure-rest-api.handler
      (:import com.mchange.v2.c3p0.ComboPooledDataSource)
      (:use compojure.core)
      (:use cheshire.core)
      (:use ring.util.response)
      (:require [compojure.handler :as handler]
                [ring.middleware.json :as middleware]
                [clojure.java.jdbc :as sql]
                [compojure.route :as route]))

    ;; DataBase Config

    (def db-config
      {:classname "org.h2.Driver"
       :subprotocol "h2"
       :subname "mem:todos"
       :user ""
       :password ""})

    ;; Pool Config

    (defn pool
      [config]
      (let [cpds (doto (ComboPooledDataSource.)
                   (.setDriverClass (:classname config))
                   (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
                   (.setUser (:user config))
                   (.setPassword (:password config))
                   (.setMaxPoolSize 1)
                   (.setMinPoolSize 1)
                   (.setInitialPoolSize 1))]
        {:datasource cpds}))

    (def pooled-db (delay (pool db-config)))

    (defn db-connection [] @pooled-db)

    (sql/with-connection (db-connection)
      (sql/create-table :todos [:id "varchar(256)" "primary key"]
                                   [:title "varchar(1024)"]
                                   [:text :varchar]))

    (defn uuid [] (str (java.util.UUID/randomUUID)))

    ;; Get All todos

    (defn get-all-todos []
      (response
        (sql/with-connection (db-connection)
          (sql/with-query-results results
            ["select * from todos"]
            (into [] results)))))

    ;; Get One todo

    (defn get-todo [id]
      (sql/with-connection (db-connection)
        (sql/with-query-results results
          ["select * from todos where id = ?" id]
          (cond
            (empty? results) {:status 404}
            :else (response (first results))))))

    ;; Create new todo

    (defn create-new-todo [doc]
      (let [id (uuid)]
        (sql/with-connection (db-connection)
          (let [todo (assoc doc "id" id)]
            (sql/insert-record :todos todo)))
        (get-todo id)))

    ;; Update One todo

    (defn update-todo [id doc]
        (sql/with-connection (db-connection)
          (let [todo (assoc doc "id" id)]
            (sql/update-values :todos ["id=?" id] todo)))
        (get-todo id))

    ;; Delete One todo

    (defn delete-todo [id]
      (sql/with-connection (db-connection)
        (sql/delete-rows :todos ["id=?" id]))
      {:status 204})

    ;; App Routes

    (defroutes app-routes
      (context "/api" [] (defroutes api-routes
        (context "/v1" [] (defroutes version-routes
          (context "/todos" [] (defroutes todos-routes
            (GET  "/" [] (get-all-todos))
            (POST "/" {body :body} (create-new-todo body))))
            (context "/:id" [id] (defroutes todo-routes
                (GET    "/" [] (get-todo id))
                (PUT    "/" {body :body} (update-todo id body))
                (DELETE "/" [] (delete-todo id))))))))
      (route/not-found "Not Found"))

    ;; App Handler

    (def app
        (-> (handler/api app-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))