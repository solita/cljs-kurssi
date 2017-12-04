(ns widgetshop.main
  "Main entrypoint for the widgetshop frontend."
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.app.products :as products]
            [widgetshop.app.navigation :as navigation]
            [widgetshop.routes :as routes]))



;; Task 1: refactor this, the listing of products in a category should
;; be its own component (perhaps in another namespace).
;;
;; Task 2: Add actions to add item to cart. See that cart badge is automatically updated.
;;

(defn add-product-to-cart [product]
  [ui/flat-button {:primary true
                   :on-click (fn [e]
                               (.stopPropagation e)
                               (products/add-to-cart! product))}
   "Add to cart"])

(def product-listing-columns
  [{:label "Name" :value-fn :name}
   {:label "Description" :value-fn :accessor}
   {:label "Price (€)" :value-fn (comp #(* 0.75 %) :price)}
   {:label "Average rating" :value-fn #(if (zero? (:stars %))
                                         "No reviews"
                                         (:stars %))}
   {:label "Add to cart" :value-fn add-product-to-cart}])

(defn product-listing [columns on-select products]
  (if (= :loading products)
    [ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

    [ui/table {:on-row-selection #(let [idx (first (js->clj %))] (on-select idx))}
     [ui/table-header {:display-select-all false :adjust-for-checkbox false}
      [ui/table-row
       (for [{label :label} columns]
         ^{:key label}
         [ui/table-header-column label])]]
     [ui/table-body {:display-row-checkbox false}
      (for [{id :id :as product} products]
        ^{:key id}
        [ui/table-row
         (for [{:keys [label value-fn]} columns]
           ^{:key label}
           [ui/table-row-column (value-fn product)])])]]))

(defn quantity-field [product]
  [ui/text-field {:value (:qty product)
                  :on-change #(let [qty (-> % .-target .-value js/parseInt)]
                                (when-not (js/isNaN qty)
                                  (products/set-product-quantity! product qty)))}])

(def cart-columns
  [{:label "Product" :value-fn :name}
   {:label "Unit price" :value-fn :price}
   {:label "Quantity" :value-fn quantity-field}
   {:label "Total" :value-fn #(* (:price %) (:qty %))}
   {:label "Remove" :value-fn (constantly "muffinsi")}])

(defn cart-page [cart]
  [:div.cart-page
   [:h3 "Shopping cart contents"
    [product-listing cart-columns (constantly false) cart]
    [:b "Grand total: " (.toFixed (reduce + (map #(* (:price %) (:qty %)) cart)) 2) " €"]
    [ui/divider]
    [ui/flat-button {:on-click #(navigation/to-product-listing)
                     :primary true}
     [ic/navigation-chevron-left]
     "Back to product listing"]
    [ui/flat-button {:on-click #(js/alert "not implemented yet")
                     :primary true
                     :style {:float "right"}}
     "Proceed to checkout"
     [ic/navigation-chevron-right]]]])


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
    "Submit review"]



   ])

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
   [ui/flat-button {:on-click navigation/to-product-listing}
    "Back to listing"]])

(defn widgetshop [{:keys [page products-by-category category selected-product] :as app}]
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                {:palette {:text-color (color :green600)}})}
   [:div
    [ui/app-bar {:title "Widgetshop!"
                 :icon-element-right
                 (r/as-element [ui/badge {:badge-content (reduce + 0 (map :qty (:cart app)))
                                          :badge-style {:top 12 :right 12}}
                                [ui/icon-button {:tooltip "Checkout"
                                                 :on-click navigation/to-shopping-cart}
                                 (ic/action-shopping-cart)]])}]
    [ui/paper

     (case page
       :product-info
       ;; Show product info page
       [product-info-page selected-product]

       :cart
       [cart-page (:cart app)]

       (:front-page :product-listing)
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
        [product-listing
         product-listing-columns
         products/select-product-by-index!
         (products-by-category category)]])]]])


(defn main-component []
  [widgetshop @state/app])

(defn ^:export main []
  (products/load-product-categories! routes/start!)
  (r/render-component [main-component] (.getElementById js/document "app")))

(defn ^:export reload-hook []
  (r/force-update-all))
