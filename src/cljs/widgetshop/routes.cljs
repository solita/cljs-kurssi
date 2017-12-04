(ns widgetshop.routes
  "Routes for frontend app."
  (:require [bide.core :as r]
            [widgetshop.app.state :as state]
            [widgetshop.app.products :as products]))

(def widgetshop-router
  (r/router
   [["/" :front-page]
    ["/products/:category" :product-listing]
    ["/product/:id" :product-details]
    ["/product/:id/review" :product-review]
    ["/cart" :cart]
    ["/checkout" :checkout]]))

;; Define a multimethod to do special app state manipulation based on
;; the page we are navigating to
(defmulti navigate :page)

(defmethod navigate :product-listing [{params :params}]
  (.log js/console "valitaaas tuotelistaus: " (pr-str params))
  (products/select-category-by-name! (:category params)))

(defmethod navigate :default [{p :page}]
  (state/update-state! assoc :page p))

(defn- on-navigate [name params query]
  (.log js/console "NAVIGATE: " (pr-str name) ", PARAMS: " (pr-str params))
  (navigate {:page name
             :params params
             :query query}))

(defn start! []
  (r/start! widgetshop-router
            {:default :front-page
             :on-navigate on-navigate}))

(defn navigate!
  "Navigate to given page with optional params."
  ([page] (navigate! page nil))
  ([page params]
   (r/navigate! widgetshop-router page params nil)))
