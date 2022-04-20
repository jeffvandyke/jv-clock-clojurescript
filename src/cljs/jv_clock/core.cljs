(ns jv-clock.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            ; [goog.dom :as dom]
            ; [goog.events :as events]
            [cljs.core.async :refer [put! chan <! >! timeout close!]]
            [jv-clock.clock-wheel :as clock-wheel]))

(enable-console-print!)

(defn get-ms [] (.getTime (js/Date. #_(2199 11 31 23 59 61))))

(defonce app-state
  (atom
    {:ms (get-ms)}) )

(defn content []
  [:div
   [:h1 "My clock here below!"]
   [:p (:ms @app-state)]
   (clock-wheel/date-information (:ms @app-state))
   (clock-wheel/svg-object (:ms @app-state))])
;; kickoff our app!
(reagent/render [content] (js/document.getElementById "app"))


;; swap the state at 20 fps
(defonce timeout-loop
  (go-loop []
           (<! (timeout (/ 1000 20)))
           (swap! app-state assoc :ms (get-ms))
           (recur))
  )
