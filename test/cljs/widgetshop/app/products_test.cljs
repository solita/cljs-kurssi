(ns widgetshop.app.products-test
  (:require [cljs.test :as test :refer-macros [deftest is testing]]
            [widgetshop.app.products :as p]))

(deftest products-by-category
  (testing "Value is set to the right path"
    (is (= (p/products-by-category {} :foo [1 2 3])
           {:products-by-category {:foo [1 2 3]}}))))

(deftest setting-categories
  (is (= (p/set-categories {:bar :baz} {:foo [] :baz []})
         {:categories {:foo [] :baz []}
          :bar :baz})))

(deftest loading-products-by-category
  (is (= (p/load-products-by-category!
           {:categories [{:id 2 :name :foo}
                         {:id 1 :name :bar}]}
           (constantly "whatever doesn't matter")
           1)
         {:category {:id 1 :name :bar}
          :categories [{:id 2 :name :foo}
                       {:id 1 :name :bar}]
          :products-by-category {{:id 1 :name :bar} :loading}})))



