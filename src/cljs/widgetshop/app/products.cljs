(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :as state]
            [widgetshop.server :as server]))

(defn- products-by-category [app category products]
  (assoc-in app [:products-by-category category] products))

(defn- set-categories [app categories]
  (assoc-in app [:categories] categories))

(defn- load-products-by-category! [{:keys [categories] :as app} server-get-fn! category-id]
  (let [category (some #(when (= (:id %) category-id) %) categories)]
    (server-get-fn! category)
    (-> app
        (assoc :category category)
        (assoc-in [:products-by-category category] :loading))))

(defn select-category-by-id! [category-id]
  (state/update-state!
    load-products-by-category!
    (fn [category]
      (server/get! (str "/products/" (:id category))
                   {:on-success #(state/update-state! products-by-category category %)}))
    category-id))

(defn load-product-categories! []
  (server/get! "/categories" {:on-success #(state/update-state! set-categories %)}))
