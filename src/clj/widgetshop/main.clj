(ns widgetshop.main
  "Main ns for widgetshop. Starts everything up."
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [widgetshop.components.http :as http]
            [widgetshop.components.db :as db]))

(def system nil)

(defn widgetshop-system [settings]
  (component/system-map
   :db (db/create-embedded-database)
   :http (http/create-http-server (get-in settings [:http :port]))))


(defn -main [& args]
  (alter-var-root #'system
                  (constantly
                   (-> "settings.edn"
                       slurp read-string
                       widgetshop-system
                       component/start-system))))
