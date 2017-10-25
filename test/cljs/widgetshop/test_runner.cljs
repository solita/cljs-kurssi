(ns widgetshop.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [widgetshop.app.products-test]))

(enable-console-print!)

(doo-tests 'widgetshop.app.products-test)