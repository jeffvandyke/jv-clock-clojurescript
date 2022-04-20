(ns jv-clock.time-info)

(def time-keys
  [:ms :sec :min :hr :day :month :short-year :century])

(defn raw-time-info [ms]
  "Returns a new time-info map with the 8 fields populated with the whole values
  at that point in time."
  (let [date (js/Date. ms)]
    {:ms (.getMilliseconds date)
     :sec (.getSeconds date)
     :min (.getMinutes date)
     :hr (.getHours date)
     :day (.getDate date)
     :month (+ 1 (.getMonth date))
     :short-year (-> date .getFullYear (mod 100))
     :century (-> date .getFullYear (/ 100) int)
     :full-year (.getFullYear date)
     }))

(defn label-for [time-key]
  "From a key in the time object, returns a string to label it."
  (case time-key
    :ms "Milliseconds" :sec "Seconds" :min "Minutes" :hr "Hour"
    :day "Date" :month "Month" :short-year "Short Year" :century "Century"
    (str "-- term \"" (name time-key) "\" not found --")))

(defn days-in-month [month year]
  (.getDate (js/Date. year month 0)))

(defn get-maxes [time-info]
  "Note: maxes are based on half-open intervals."
  {:ms 1000
   :sec 60
   :min 60
   :hr 24
   :day (+ 1 (days-in-month (:month time-info) (:full-year time-info)))
   :month 13
   :short-year 100
   :century 100}
  )

(def mins
  {:ms 0 :sec 0 :min 0 :hr 0
   :day 1 :month 1
   :short-year 0 :century 0})


(defn map-to-ratios [time-info]
  "From a time-info, returns smooth values for what those keys
  can be viewed in a slider as. Vals range from 0.0 to 0.999..."
  (let [maxes (get-maxes time-info) mins mins]
    (loop [cur-key (first time-keys)
           prev-ratio 0.0
           prev-count 1
           rems (rest time-keys)
           ratio-map {}]
      (if (nil? cur-key) ratio-map
        (let [cur-int (cur-key time-info)
              cur-min (cur-key mins)
              cur-max (cur-key maxes)
              cur-count (- cur-max cur-min)
              cur-ratio (/ (- (+ prev-ratio cur-int) cur-min)
                           cur-count)]
          (recur (first rems)
                 cur-ratio
                 cur-count
                 (rest rems)
                 (assoc ratio-map cur-key cur-ratio)))))))

