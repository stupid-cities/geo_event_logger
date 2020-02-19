(ns geo-event-logger.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]

            [ring.adapter.jetty :as ring]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]

            [cheshire.core :as json]

            [geo-event-logger.migrate :as schema]
            [geo-event-logger.events  :as events]
            [geo-event-logger.db      :as db]
            )
  (:gen-class))

(defn log-event [event]
  (events/create event)
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "{}"})

(defn get-events []
  (let [events (events/all)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body    (json/generate-string events)}))

(defn health-check []
  (db/ping)
  {:status 200 :body "OK"})

(defroutes routes
  (GET "/-/health" [] (health-check))
  (POST "/events"  [event] (log-event event))
  (GET  "/events"  [] (get-events))
  (ANY "*"         [] {:status 404}))

(def app
  (->
   routes
   handler/site
   (middleware/wrap-json-body {:keywords? true})
   middleware/wrap-json-response))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
