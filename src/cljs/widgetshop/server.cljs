(ns widgetshop.server
  "Server communication"
  (:require [ajax.core :as ajax]))

(defn get! [url {:keys [params on-success on-failure]}]
  (ajax/GET url {:params params
                 :handler on-success
                 :error-handler on-failure
                 :response-format :transit}))

(defn post! [url {:keys [body on-success on-failure]}]
  (ajax/POST url {:params body
                  :handler on-success
                  :error-handler on-failure
                  :format (ajax/transit-request-format)
                  :response-format :transit}))
