(ns widgetshop.components.db
  "Database connection pool component"
  (:import (com.zaxxer.hikari HikariConfig HikariDataSource)
           (com.opentable.db.postgres.embedded PreparedDbProvider DatabasePreparer))
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(defn- create-hikari-datasource [{:keys [url username password] :as config}]
  (HikariDataSource.
   (doto (HikariConfig.)
     (.setJdbcUrl url)
     (.setUsername username)
     (.setPassword password)
     (.addDataSourceProperty "cachePrepStmts" "true")
     (.addDataSourceProperty "prepStmtCacheSize" "250")
     (.addDataSourceProperty "prepStmtCacheSqlLimit" "2048"))))

(defrecord Database [datasource config]
  component/Lifecycle
  (start [this]
    (assoc this :datasource (create-hikari-datasource config this)))
  (stop [{ds :datasource :as this}]
    (.close ds)
    (assoc this :datasource nil)))

(defn create-database [config]
  (->Database nil config))


;; Alternate embedded version

(defrecord EmbeddedDatabase [datasource provider]
  component/Lifecycle
  (start [this]
    (assoc this :datasource (.createDataSource provider)))
  (stop [{ds :datasource :as this}]
    (.close ds)
    (assoc this :datasource nil)))

(defn create-embedded-database []
  (->EmbeddedDatabase
   nil
   (PreparedDbProvider/forPreparer
    (reify DatabasePreparer
      (prepare [this ds]
        (doseq [sql (remove str/blank? (-> "resources/database.sql"
                                           slurp (str/split #";")))]
          (println "INIT SQL: " sql)
          (jdbc/execute! {:datasource ds} [sql])))))))
