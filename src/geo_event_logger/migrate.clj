(ns geo-event-logger.migrate
  (:require [clojure.java.jdbc :as sql]

            [clj-postgresql.core :as pg]

            [geo-event-logger.events :as events]
            [geo-event-logger.db :as db]
            ))

(defn migrated? []
  (-> (sql/query db/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='events'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)

;;    (doseq [extension [:postgis :postgis_topology :hstore]] (sql/db-do-commands db/spec (str "CREATE EXTENSION " extension ";")))

    (sql/db-do-commands db/spec
                        (sql/create-table-ddl
                         :events
                         [[:id         :serial                  "PRIMARY KEY"]
                          [:geo        "geography(POINT,4326)"  "NOT NULL"]
                          [:resource   :varchar            ""]
                          [:category   :int                ""]
                          [:created_at :timestamp          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]))
    (println " done")))

(comment
  (migrate)
  )