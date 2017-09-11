(ns widgetshop.main
  "Main entrypoint for the widgetshop frontend."
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :refer [app]]
            [widgetshop.app.products :as products]))



;; Task 1: refactor this, the listing of products in a category should
;; be its own component (perhaps in another namespace).
;;
;; Task 2: Add actions to add item to cart. See that cart badge is automatically updated.
;;

(defn widgetshop [app]
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                {:palette {:text-color (color :green600)}})}
   [:div
    [ui/app-bar {:title "Widgetshop!"
                 :icon-element-right
                 (r/as-element [ui/badge {:badge-content (count (:cart app))
                                          :badge-style {:top 12 :right 12}}
                                [ui/icon-button {:tooltip "Checkout"}
                                 (ic/action-shopping-cart)]])}]
    [ui/paper

     (let [products ((:products-by-category app) (:category app))]
       (if (= :loading products)
         [ui/refresh-indicator {:status "loading" :size 40}]

         [ui/table
          [ui/table-header
           [ui/table-row
            [ui/table-header-column "Name"]
            [ui/table-header-column "Description"]
            [ui/table-header-column "Price (€)"]
            [ui/table-header-column "Add to cart"]]]
          [ui/table-body
           (for [{:keys [id name description price]} ((:products-by-category app) (:category app))]
             ^{:key id}
             [ui/table-row
              [ui/table-row-column name]
              [ui/table-row-column description]
              [ui/table-row-column price]
              [ui/table-row-column
               [ui/flat-button {:primary true :on-click #(js/alert "add to cart!")}
                "Add to cart"]]])]]))

     [ui/raised-button {:label        "Click me"
                        :icon         (ic/social-group)
                        :on-click     #(println "clicked")}]]]])


(defn main-component []
  [widgetshop @app])

(defn ^:export main []
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
