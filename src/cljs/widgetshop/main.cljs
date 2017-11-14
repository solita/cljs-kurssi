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

(defn product-listing [products]
  (if (= :loading products)
    [ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

    [ui/table {:on-row-selection #(let [idx (first (js->clj %))]
                                    (products/select-product-by-index! idx))}
     [ui/table-header {:display-select-all false :adjust-for-checkbox false}
      [ui/table-row
       [ui/table-header-column "Name"]
       [ui/table-header-column "Description"]
       [ui/table-header-column "Price (€)"]
       [ui/table-header-column "Add to cart"]]]
     [ui/table-body {:display-row-checkbox false}
      (for [{:keys [id name description price] :as product} products]
        ^{:key id}
        [ui/table-row
         [ui/table-row-column name]
         [ui/table-row-column description]
         [ui/table-row-column price]
         [ui/table-row-column
          [ui/flat-button {:primary true
                           :on-click (fn [e]
                                       (.stopPropagation e)
                                       (products/add-to-cart! product))}
           "Add to cart"]]])]]))

(defn product-review [{:keys [stars comment submit-in-progress?]}]
  [:div.product-review
   [:h3 "Your review"]
   (doall
    (for [s (range 1 6)
          :let [set-stars! #(products/update-product-review-stars! s)]]
      [:span.stars {:key s}
       (if (>= stars s)
         [ic/toggle-star {:on-click set-stars!}]
         [ic/toggle-star-border {:on-click set-stars!}])]))

   [ui/text-field {:value (or comment "")
                   :floating-label-text "Review comment"
                   :on-change (fn [event value]
                                (products/update-product-review-comment! value))}]
   [ui/flat-button {:primary true
                    :disabled submit-in-progress?
                    :on-click products/submit-review!}
    "Submit review"]])

(defn product-info-page [product]
  [:div
   [:h3 "Product info"]
   [:div.product-info
    [:div [:b "Name: "] (:name product)]
    [:div [:b "Description: "] (:description product)]
    [:div [:b "Price: "] (:description product)]]
   (if-let [review (:review product)]
     [product-review (:review product)]
     [ui/flat-button {:on-click #(products/start-review!)} "Review"])
   [ui/flat-button {:on-click products/remove-product-selection!}
    "Back to listing"]])

(defn widgetshop [{:keys [products-by-category category selected-product] :as app}]
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

     (if selected-product
       ;; Show product info page
       [product-info-page selected-product]

       ;; Show product category selection and listing
       [:span
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
        [product-listing (products-by-category category)]])]]])


(defn main-component []
  [widgetshop @state/app])

(defn ^:export main []
  (products/load-product-categories!)
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
