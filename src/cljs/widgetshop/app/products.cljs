(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :refer [update-state! set-state!]]
            [widgetshop.server :as server]))

(defn select-category! [category]
  (set-state! [:category] category)
  (set-state! [:products-by-category category] :loading)
  (server/get! (str "/products/" category)
               {:on-success #(set-state! [:products-by-category category] %)}))
