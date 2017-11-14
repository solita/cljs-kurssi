(ns widgetshop.services.products
  (:require [widgetshop.components.http :refer [publish! transit-response transit-request]]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [clojure.java.jdbc :as jdbc]))

(defn fetch-products-for-category [db category]
  (into []
        (map #(update % :price double))
        (jdbc/query db [(str "SELECT p.id,p.name,p.description,p.price"
                             "  FROM product p"
                             "  JOIN product_category pc ON pc.product_id = p.id "
                             " WHERE pc.category_id = ?")
                        category])))

(defn fetch-product-categories [db]
  (jdbc/query db ["SELECT c.id, c.name, c.description FROM category c"]))

(defn save-product-review! [db {:keys [product-id stars comment] :as review}]
  (jdbc/insert! db :product_review {:product_id product-id
                                    :stars stars
                                    :comment comment}))

(defn fetch-product-reviews [db product-id]
  (jdbc/query db ["SELECT * FROM product_review WHERE product_id=?" product-id]))

(defrecord ProductsService []
  component/Lifecycle
  (start [{:keys [db http] :as this}]
    (assoc this ::routes
           (publish! http
                     (routes
                      (GET "/categories" []
                           (transit-response
                            (fetch-product-categories db)))
                      (GET "/products/:category" [category]
                           (transit-response
                            (fetch-products-for-category db (Long/parseLong category))))
                      (POST "/review" {body :body}
                            (transit-response
                             (save-product-review! db (transit-request body))))))))
  (stop [{stop ::routes :as this}]
    (stop)
    (dissoc this ::routes)))
