(ns widgetshop.components.http
  (:require [org.httpkit.server :as server]
            [com.stuartsierra.component :as component]))

(defn- serve-request [handlers req]
  ((apply some-fn @handlers) req))

(defrecord HttpServer [http-kit-options handlers]
  component/Lifecycle
  (start [this]
    (assoc this ::stop (server/run-server (partial serve-request handlers) http-kit-options)))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn create-http-server [port]
  (->HttpServer {:port port} (atom #{})))

(defn publish [{handlers :handlers} handler]
  (swap! handlers conj handler)
  #(swap! handlers disj handler))
