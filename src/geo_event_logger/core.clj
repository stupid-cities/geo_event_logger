(ns geo-event-logger.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]

            [ring.adapter.jetty :as ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]

            [cheshire.core :as json]

            [geo-event-logger.migrate :as schema]
            [geo-event-logger.events :as events]
            )
  (:gen-class))

(defn log-event []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "{}"})

(defn get-events []
  (let [events (events/all)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body    (json/generate-string events)}))

(defroutes routes
  (POST "/events" [] (log-event))
  (GET  "/events" [] (get-events))
  (ANY "*"        [] {:status 404}))

(def app
  (->
   routes
   handler/site
   (middleware/wrap-json-body {:keywords? true})
   middleware/wrap-json-response))

(defn start [port]
  (ring/run-jetty application {:port port :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
