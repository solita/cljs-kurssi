(defproject cljs-kurssi "0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha20"]

                 ;; Component library
                 [com.stuartsierra/component "0.3.2"]

                 ;; Logging library
                 [com.taoensso/timbre "4.10.0"]

                 ;; PostgreSQL JDBC driver
                 [org.postgresql/postgresql "42.1.4"]

                 ;; http-kit HTTP server (and client)
                 [http-kit "2.2.0"]]

  ;; Sources for backend: clj and cljc (shared with frontend)
  :source-paths ["src/clj" "src/cljc"])
