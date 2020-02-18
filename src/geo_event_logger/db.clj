(ns geo-event-logger.db
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/events"))

(defn ping [] (sql/query spec ["select true"]))