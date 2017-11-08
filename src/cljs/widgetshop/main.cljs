(ns widgetshop.main
  "Main entrypoint for the widgetshop frontend."
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
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

     ;; Product category selection
     (when-not (= :loading (:categories app))
       [ui/select-field {:floating-label-text "Select product category"
                         :value (:id (:category app))
                         :on-change (fn [evt idx value]
                                      (products/select-category-by-id! value))}
        (for [{:keys [id name] :as category} (:categories app)]
          ^{:key id}
          [ui/menu-item {:value id :primary-text name}])])

     ;; Product listing for the selected category
     (let [products ((:products-by-category app) (:category app))]
       (if (= :loading products)
         [ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

         [ui/table
          [ui/table-header {:display-select-all false :adjust-for-checkbox false}
           [ui/table-row
            [ui/table-header-column "Name"]
            [ui/table-header-column "Description"]
            [ui/table-header-column "Price (€)"]
            [ui/table-header-column "Add to cart"]]]
          [ui/table-body {:display-row-checkbox false}
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
  [widgetshop @state/app])

(defn ^:export main []
  (products/load-product-categories!)
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
