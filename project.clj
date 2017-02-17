(defproject clojure-rest-api "0.1.0-SNAPSHOT"
  :description "simple REST services"
  :url ""
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.1.2"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2databse/h2 "1.3.168"]
                 [chesire "4.0.3"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clojure-rest-api.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
