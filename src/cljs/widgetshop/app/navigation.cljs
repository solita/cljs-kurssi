(ns widgetshop.app.navigation
  (:require [widgetshop.app.state :as state]))


(defn to-shopping-cart
  "Navigate to shopping cart page."
  []
  (state/update-state! assoc :page :cart))

(defn to-product-listing
  "Navigate to product listing page."
  []
  (state/update-state! assoc :page :product-listing))
