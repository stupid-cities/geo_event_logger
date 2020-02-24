(ns geo-event-logger.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [s3-beam.handler :as s3b]

            [ring.adapter.jetty :as ring]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params   :refer [wrap-params]]

            [cheshire.core :as json]
            [crypto.equality :as crypto]

            [geo-event-logger.migrate :as schema]
            [geo-event-logger.events  :as events]
            [geo-event-logger.db      :as db])
  (:gen-class))

(defn api-key [] (System/getenv "API_KEY"))

(defn valid-api-key? [k]
  (when (and k (api-key))
    (crypto/eq? k (api-key))))

(defn log-event [event]
  (when (and
         (valid-api-key? (get-in event [:api_key]))
         (events/valid? event))
    (let [success (events/create event)]
      (if success
        {:status 200 :body event}
        {:status 500}))))

(defn get-events []
  (let [events (events/all)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body    (json/generate-string events)}))

(defn health-check []
  (db/ping)
  {:status 200 :body "OK"})

(defroutes routes
  (GET  "/-/health" [] (health-check))
  (GET  "/resource/sign"  {params :params} (s3b/s3-sign (store/bucket) (store/aws-zone) (store/access-key) (store/secret-key)))
  (POST "/events"  {event :params} (log-event event))
  (GET  "/events"  [] (get-events))
  (ANY "*"         [] {:status 404}))

(def app
  (->
   routes
   wrap-params
   handler/site
   (middleware/wrap-json-body {:keywords? true})
   middleware/wrap-json-response))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
