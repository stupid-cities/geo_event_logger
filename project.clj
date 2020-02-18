(defproject geo-event-logger "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]

                 [cheshire "5.10.0"]

                 [environ "1.1.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]

  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]
            [lein-ring "0.12.5"]]

  :main ^:skip-aot geo-event-logger.core

  :uberjar-name "geo-event-logger-standalone.jar"

  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}
             :uberjar {:aot :all}}

  :ring {:handler geo-event-logger.core/app
         :init    geo-event-logger.migrate/migrate}
  )
