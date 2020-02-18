(ns geo-event-logger.events
  (:require [clojure.java.jdbc :as sql]
            [geo-event-logger.db :as db]
            ))

(defn all []
  (into [] (sql/query db/spec ["select * from events order by id desc"])))

(defn create [event] (sql/insert! db/spec :events [:body] [event]))