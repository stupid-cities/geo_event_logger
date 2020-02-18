(ns geo-event-logger.events
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/events"))

(defn all []
  (into [] (sql/query spec ["select * from events order by id desc"])))

(defn create [event] (sql/insert! spec :events [:body] [event]))