(ns widgetshop.app.navigation
  (:require [widgetshop.app.state :as state]
            [widgetshop.routes :refer [navigate!]]))


(defn to-shopping-cart
  "Navigate to shopping cart page."
  []
  #_(state/update-state! assoc :page :cart)
  (navigate! :cart {}))

(defn to-product-listing
  "Navigate to product listing page."
  []
  #_(state/update-state! assoc :page :product-listing)
  (let [category (some-> @state/app :category :name)]
    (if category
      (navigate! :product-listing {:category category})
      (navigate! :front-page {}))))
