(ns geo-event-logger.store)

(defn bucket     [] (System/getenv "S3_BUCKET"))
(defn aws-zone   [] (System/getenv "S3_ZONE"))
(defn access-key [] (System/getenv "S3_ACCESS_KEY"))
(defn secret-key [] (System/getenv "S3_SECRET_KEY"))
