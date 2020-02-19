(ns geo-event-logger.events
  (:require [clojure.java.jdbc :as sql]
            [geo-event-logger.db :as db]

            [clj-postgresql.spatial :as spatial]
            ))

(defn all []
  (into [] (sql/query db/spec ["select * from events order by id desc"])))

(defn valid? [event]
  (and (contains? event :lng)
       (contains? event :lat)))

;; * srid 4326
;;INSERT INTO events (geo) VALUES (ST_GeomFromText('POINT(-126.4 45.32)', 4326));


(defn create [event]
  (when (valid? event)
    (try
      (let [clean-event (->
                         event
                         (select-keys [:lng :lat :resource])
                         (assoc :geo (spatial/point (Double/parseDouble (:lng event))    ;;This feels dangerous for precision
                                                    (Double/parseDouble (:lat event))))
                         (dissoc :lng)
                         (dissoc :lat))]
        (sql/insert! db/spec :events clean-event))
      (catch Exception e
        nil))))

(comment
  (valid? {})
  (valid? {:lng 1 :lat 2})

  (spatial/point "1" "2")

  (Double/parseDouble "-1.1")


  (create {:lng "-1.1" :lat "2.2"})

  (create {:lng 1 :lat 2})
  (all)
  )