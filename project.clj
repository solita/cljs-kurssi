(defproject cljs-kurssi "0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha20"]

                 ;; Component library
                 [com.stuartsierra/component "0.3.2"]

                 ;; Logging library
                 [com.taoensso/timbre "4.10.0"]

                 ;; PostgreSQL JDBC driver and connection pooling
                 [org.postgresql/postgresql "42.1.4"]
                 [com.zaxxer/HikariCP "2.6.1"]
                 [org.clojure/java.jdbc "0.7.1"]

                 ;; embedded postgres
                 [com.opentable.components/otj-pg-embedded "0.7.1"]

                 ;; http-kit HTTP server (and client)
                 [http-kit "2.2.0"]]



  ;; Sources for backend: clj and cljc (shared with frontend)
  :source-paths ["src/clj" "src/cljc"])
