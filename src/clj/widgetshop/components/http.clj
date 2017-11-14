(ns widgetshop.components.http
  (:require [org.httpkit.server :as server]
            [com.stuartsierra.component :as component]
            [compojure.route :refer [resources]]
            [cognitect.transit :as transit]))

(defn- serve-request [handlers req]
  ((apply some-fn handlers) req))

(defrecord HttpServer [http-kit-options handlers]
  component/Lifecycle
  (start [this]
    (let [resource-handler (resources "/")]
      (assoc this ::stop (server/run-server
                          (fn [req]
                            (serve-request (conj @handlers resource-handler) req))
                          http-kit-options))))

  (stop [{stop ::stop :as this}]
    (stop)
    (dissoc this ::stop)))

(defn create-http-server [port]
  (->HttpServer {:port port} (atom [])))

(defn publish! [{handlers :handlers} handler]
  (swap! handlers conj handler)
  #(swap! handlers filterv not= handler))

(defn transit-response [data]
  {:status 200
   :headers {"Content-Type" "application/json+transit"}
   :body (with-open [out (java.io.ByteArrayOutputStream.)]
           (transit/write (transit/writer out :json) data)
           (str out))})

(defn transit-request
  "Parse HTTP POST body as Transit data."
  [in]
  (transit/read (transit/reader in :json)))
