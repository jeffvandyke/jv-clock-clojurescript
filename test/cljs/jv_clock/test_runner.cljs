(ns jv-clock.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [jv-clock.core-test]))

(enable-console-print!)

(doo-tests 'jv-clock.core-test)
