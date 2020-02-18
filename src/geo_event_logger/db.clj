(ns geo-event-logger.db)

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/events"))
