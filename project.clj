(defproject cljs-kurssi "0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha19"]
                 [org.clojure/clojurescript "1.9.946"]

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
                 [http-kit "2.2.0"]

                 ;; Routing library for publishing services
                 [compojure "1.6.0"]

                 ;; Transit data format libraries for backend and frontend
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.239"]

                 ;; Ajax library for frontend
                 [cljs-ajax "0.7.2"]


                 ;; Frontend UI-libraries
                 [reagent "0.7.0"]
                 [cljsjs/react "15.6.1-1"]
                 [cljsjs/react-dom "15.6.1-1"]
                 [cljs-react-material-ui "0.2.48"]
                 [figwheel "0.5.14"]

                 ;; Something pulls an old guava which prevents closure compiler
                 ;; override here
                 [com.google.guava/guava "21.0"]
                 [binaryage/devtools "0.9.7"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.14"]]

  ;; Sources for backend: clj and cljc (shared with frontend)
  :source-paths ["src/clj" "src/cljc"]

  ;; Configure ClojureScript builds
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/cljc"]
                :figwheel {:on-jsload "widgetshop.main/reload-hook"}
                :compiler {:optimizations :none
                           :source-map true
                           :output-to "resources/public/js/widgetshop.js"
                           :output-dir "resources/public/js/out"
                           :preloads [devtools.preload]}}]}

  :main widgetshop.main)
