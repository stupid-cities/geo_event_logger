(ns geo-event-logger.migrate
  (:require [clojure.java.jdbc :as sql]
            [geo-event-logger.events :as events]))

(defn migrated? []
  (-> (sql/query events/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='events'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands geo-event-logger.events/spec
                        (sql/create-table-ddl
                         :events
                         [[:id    :serial "PRIMARY KEY"]
                          [:long  :varchar "NOT NULL"]
                          [:lat   :varchar "NOT NULL"]
                          [:image :varchar "NOT NULL"]
                         [:created_at :timestamp
                          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]))
    (println " done")))