(ns advent.4
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import java.time.format.DateTimeFormatter
           java.time.LocalDateTime
           java.time.temporal.ChronoUnit))

(def input (-> "input/4.txt"
               io/resource
               slurp
               (str/split #"\n")))

(defn parse-event [s]
  (let [regex #"\[(\d\d\d\d\-\d\d\-\d\d\s\d\d:\d\d)\]\s(Guard\s\#(\d+)\s)?(wakes|falls|begins).*"
        [_ timestamp _ guard-id event-type] (re-matches regex s)
        formatter (DateTimeFormatter/ofPattern "y-M-d H:m")]
    {:timestamp (LocalDateTime/parse timestamp formatter)
     :guard-id guard-id
     :event-type event-type}))

(def events (->> input
                 (map parse-event)
                 (sort-by :timestamp)))

(defn safe-add [a b]
  (+ (or a 0) b))

(defn duration [t1 t2]
  (.until t1 t2 ChronoUnit/SECONDS))

(defn minutes-between [t1 t2]
  (if (>= (duration t1 t2) 3600)
    (range 0 60)
    (let [t1m (.getMinute t1)
          t2m (.getMinute t2)]
      (if (< t1m t2m)
        (range t1m (inc t2m))
        (concat (range t1m 60)
                (range 0 (inc t2m)))))))

(defn asleep-intervals [{:keys [guard->minute guard->duration current-guard last-asleep]} event]
  (let [current-guard (or (:guard-id event) current-guard)
        minute (.getMinute (:timestamp event))
        slept-for (when (= (:event-type event) "wakes")
                    (duration last-asleep (:timestamp event)))
        last-asleep (if (= (:event-type event) "falls")
                      (:timestamp event)
                      last-asleep)]
    {:guard->minute (if slept-for
                      (update guard->minute current-guard concat (minutes-between
                                                                      last-asleep
                                                                      (:timestamp event)))
                      guard->minute)
     :guard->duration (if slept-for
                        (update guard->duration current-guard safe-add slept-for)
                        guard->duration)
     :current-guard current-guard
     :last-asleep last-asleep}))

(defn build-timeline []
  (reduce asleep-intervals {:guard->minute {} :guard->duration {}} events))

(defn main-4 []
  (let [timeline (build-timeline)
        by-duration (sort-by val (:guard->duration timeline))
        minutes (sort-by val (frequencies (get-in timeline [:guard->minute (key (last by-duration))])))]
    (*
     (Integer/parseInt (key (last by-duration)))
     (key (last minutes)))))


(defn main-4-2 []
  (let [timeline (build-timeline)
       best-guard-by-minute 


        (reduce (fn [minute->best-guard [guard minute freq]]
                  (let [[old-guard old-freq] (minute->best-guard minute)]
                    (if (or (nil? old-freq) (> freq old-freq))
                      (assoc minute->best-guard minute [guard freq])
                      minute->best-guard)))
                {}
                (mapcat (fn [[guard minutes]]
                          (for [[minute freq] (frequencies minutes)]
                            [guard minute freq]))
                        (:guard->minute timeline)))]

    (sort-by #(second (second %))
             best-guard-by-minute)))
