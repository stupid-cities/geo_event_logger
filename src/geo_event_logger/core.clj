(ns geo-event-logger.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]

            [geo-event-logger.migrate :as migrate])
  (:gen-class))

(defn log-event []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "{}"})

(defroutes app
  (POST "/event" [] (log-event))
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (migrate/migrate)
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
