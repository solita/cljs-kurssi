(ns widgetshop.app.state
  "Defines the application state atom"
  (:require [reagent.core :as r]))

(defonce app (r/atom {:cart []
                      :categories :loading ;; list of product categories
                      :category nil ;; the selected category

                      ;; Loaded product listings keyd by selected category
                      :products-by-category {}}))

(defn update-state! [update-fn & args]
  (swap! app
         (fn [current-app-state]
           (apply update-fn current-app-state args))))

(defn set-state! [path value]
  (swap! app assoc-in path value))
