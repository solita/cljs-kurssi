(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :refer [update-state! set-state!]]
            [widgetshop.server :as server]))

(defn select-category-by-id! [category-id]
  (update-state!
   (fn [{:keys [categories] :as app}]
     (let [category (some #(when (= (:id %) category-id) %) categories)]
       (server/get! (str "/products/" (:id category))
                    {:on-success #(set-state! [:products-by-category category] %)})
       (-> app
           (assoc :category category)
           (assoc-in [:products-by-category category] :loading))))))

(defn load-product-categories! []
  (server/get! "/categories" {:on-success #(set-state! [:categories] %)}))
