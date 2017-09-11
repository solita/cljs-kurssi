(ns widgetshop.app.state
  "Defines the application state atom"
  (:require [reagent.core :as r]))

(defonce app (r/atom {:cart []
                      :categories [] ;; list of product categories
                      :category "toys" ;; the selected category
                      :products-by-category {"toys" [{:id 1
                                                      :name "log from blammo"
                                                      :description "It's log, it's big, it's heavy, it's wood."
                                                      :price 4.55}
                                                     {:id 2
                                                      :name "don't whizz on the electric fence"
                                                      :description "Fun game for the whole family"
                                                      :price 70.95}]}}))

(defn update-state! [path fn & args]
  (swap! app update-in path
         #(apply fn % args)))

(defn set-state! [path value]
  (swap! app assoc-in path value))
