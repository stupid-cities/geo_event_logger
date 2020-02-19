(ns geo-event-logger.events
  (:require [clojure.java.jdbc :as sql]
            [geo-event-logger.db :as db]
            ))

(defn all []
  (into [] (sql/query db/spec ["select * from events order by id desc"])))

(defn create [event]
  (let [clean-event (select-keys [:longlat :image] event)]
    (sql/insert! db/spec :events [:body] [clean-event])))
