(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :as state]
            [widgetshop.server :as server]
            [clojure.string :as str]))

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

(defn select-category-by-name! [category-name]
  (let [category-name (str/lower-case category-name)
        id (some #(when (= (str/lower-case (:name %))
                           category-name)
                    (:id %))
                 (:categories @state/app))]
    (when id
      (select-category-by-id! id))))

(defn load-product-categories! [then]
  (server/get! "/categories" {:on-success
                              #(do
                                 (state/update-state! set-categories %)
                                 (then))}))


(defn- product-index-in-cart [old-cart product]
  (first (keep-indexed (fn [idx cart-product]
                         (when (= (:id product) (:id cart-product))
                           idx))
                       old-cart)))

(defn add-to-cart! [product]
  (.log js/console "adding product to cart " (pr-str product))
  (state/update-state!
   (fn [app]
     (let [old-cart (:cart app)
           idx (product-index-in-cart old-cart product)
           new-cart (if idx
                      (update old-cart idx update :qty inc)
                      (conj old-cart (assoc product :qty 1)))]
       (assoc app :cart new-cart)))))

(defn set-product-quantity! [product new-quantity]
  (state/update-state!
   (fn [app]
     (update app :cart
             (fn [old-cart]
               (update old-cart (product-index-in-cart old-cart product) assoc :qty new-quantity))))))

(defn find-product-by-index [app idx]
  (let [category (:category app)
        products-of-category ((:products-by-category app) category)
        product-by-index (nth products-of-category idx)]
    product-by-index))

(defn select-product-by-index! [idx]
  (state/update-state!
   (fn [app]
     (assoc app
            :selected-product (find-product-by-index app idx)
            :page :product-info))))

(defn remove-product-selection! []
  (state/update-state! dissoc :selected-product))

(defn update-product-review-comment! [comment]
  (state/update-state!
   assoc-in
   [:selected-product :review :comment] comment))

(defn update-product-review-stars! [stars]
  (state/update-state!
   assoc-in
   [:selected-product :review :stars] stars))

(defn start-review! []
  (state/update-state! assoc-in [:selected-product :review] {:comment "" :stars 0}))

(defn submit-review! []
  (state/update-state!
   (fn [app]
     (let [review (assoc (get-in app [:selected-product :review])
                         :product-id (get-in app [:selected-product :id]))]
       (.log js/console "REVIEW:" (pr-str review))
       (server/post! "/review"
                    {:body review
                     :on-success #(.log js/console "onnistui" (pr-str %))
                     :on-failure #(.log js/console "ei onnistu")})
       (assoc-in app [:selected-product :review :submit-in-progress?] true))))

  ;; 1. lähetä review palvelimelle
  ;; 2. aseta tilaan lippu, että lähetys käynnissä
  ;; 3. on-success handlerissa poistetaan lippu ja reviewn tiedot
  )



(defn set-selected-product-id! [id]
  (state/update-state! assoc :selected-product-id id))
