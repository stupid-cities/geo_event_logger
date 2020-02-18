(ns geo-event-logger.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]

            [ring.adapter.jetty :as ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]

            [geo-event-logger.migrate :as schema])
  (:gen-class))

(defn log-event []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "{}"})

(defroutes routes
  (POST "/event" [] (log-event))
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))

(def application (wrap-defaults routes site-defaults))

(defn start [port]
  (ring/run-jetty application {:port port :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))