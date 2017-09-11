(ns widgetshop.services.products
  (:require [widgetshop.components.http :refer [publish! transit-response]]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]))

(defn fetch-products-for-category [db category]
  (jdbc/query db [(str "SELECT id,name,description,price"
                       "  FROM products"
                       " WHERE ")]) "FOO")

(defrecord ProductsService []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc this ::routes
           (publish! http
                     (routes
                      (GET "/products/:category" [category]
                           (transit-response (fetch-products-for-category db category)))))))
  (stop [{stop ::routes :as this}]
    (stop)
    (dissoc this ::routes)))
