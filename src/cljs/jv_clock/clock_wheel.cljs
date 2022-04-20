(ns jv-clock.clock-wheel
  (:require [reagent.core :as reagent]
            [jv-clock.time-info :as t]))

(def adjustment
  {:hr 0
   })

(defn get-time-info [ms]
  (let [time-info (t/raw-time-info ms)
        adjusted-time (merge-with + time-info adjustment)
        ratios (t/map-to-ratios adjusted-time)
        maxes (t/get-maxes adjusted-time)
        mins t/mins
        values (merge-with #(str %1 ", " %2)
                           ratios)]
    {:time-info time-info
     :adjusted-time adjusted-time
     :ratios ratios
     :maxes maxes
     :mins mins
     :values values}))


(defn date-information [ms]
  (let [time-info (get-time-info ms)]
    nil #_[:dl
     [:dt "date string"]
     [:dd (.toString (js/Date. ms))]
     (for [k (reverse t/time-keys)
           :let [v (time-info k)]]
       [:span {:key k}
        [:dt (t/label-for k)]
        [:dd v]]
       )
     ]
    ))


(defn get-time-floats [ms]
  (let [time-info (t/raw-time-info ms)
        adjusted-time (merge-with + time-info adjustment)
        ratios (t/map-to-ratios adjusted-time)]
    ratios))


;; used for the ring to display smooth times
;; while not updating beyond a certain point.
(defn round [prec number]
  (let [mult (js/Math.pow 10 prec)]
    (/ (int (* mult number)) mult)))


(def get-labels
  (memoize (fn [min max mode]
            (cond
              (= mode :month)
              '("Jan" "Feb" "Mar" "Apr" "May" "Jun"
                      "Jul" "Aug" "Sep" "Oct" "Nov" "Dec")
              (or (= mode :short-year) (= mode :century))
              (map
                #(if (= 0 (mod % 10)) % "2")
                (range min max))
              :else
              (range min max))
            )))

;; the ring component, used with options to display one of the
;; 7 or so rings used for displaying the clock.
(defn ring [key progress index min-label max-label]
  (let [offset (* 50 index)
        labels (get-labels min-label max-label key)]
    [:g {:key index
         :transform (str "rotate(" (round 2 (* 360 progress)) ")")}
     [:circle {:cx 0 :cy 0 :r offset
               :stroke "black" :stroke-width 2
               :fill "none"}]
     [:circle {:cx 0 :cy 0 :r (+ 49 offset)
               :stroke "black" :stroke-width 2
               :fill "none"}]
     (map (fn [label index]
            [:g {:key label
                 :transform (str "rotate("
                                 (* -360 (/ index (count labels))) ")")}
             [:text {:x 2 :y (+ 15 offset)} label]
             [:line {:x1 0 :y1 offset
                     :x2 0 :y2 (+ 20 offset)
                     :stroke "gray"
                     :stroke-width 1.5
                     }]])
          labels
          (range (count labels)))
     [:line {:x1 0 :y1 offset
             :x2 0 :y2 (+ 50 offset)
             :stroke "black"
             :stroke-width 3}]] ))


(defn svg-object [ms]
  (let [time-info (get-time-info ms)
        time-floats (:ratios time-info)
        time-maxes (:maxes time-info)
        time-mins (:mins time-info)]
    [:svg {:view-box "-500 -500 1000 1000" ; 1000x1000 centered at origin
           :id "clock-wheel-svg"
           :style {:width "500px" :height "500px"}}
     (map (fn [key index]
            (ring key
                  (key time-floats)
                  index
                  (key time-mins)
                  (key time-maxes)))
          (rest t/time-keys)
          (reverse (range 1 (count t/time-keys))))
     ]))
