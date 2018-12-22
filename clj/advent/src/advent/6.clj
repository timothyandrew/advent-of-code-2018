(ns advent.6
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))


(def input (-> "input/6.txt"
               io/resource
               slurp
               (str/split #"\n")))

(defn ->int [n]
  (Integer/parseInt n))

(defn get-inputs []
  (map #(map (comp ->int str/trim) (str/split % #",")) input))

(defn map-values [f m]
     (into {} (for [[k v] m] [k (f v)])))

;; From (0,0) to (500,500), find distance between each point and each input point, recording maximum.
;; From (-1000,-1000) to (1000, 1000), find distance of each point and each input point, recording the maximum.
;; From first list, remove any areas that grew larger in the second list
;; Largest remaining area is the answer

(defn p [x y] {:x x :y y})

(defn unique-minimum? [c]
  (let [minimum (apply min c)]
    (= 1 (count (filter #(= % minimum) c)))))

(defn distance [p1 p2]
  (+ (Math/abs (- (:x p1) (:x p2)))
     (Math/abs (- (:y p1) (:y p2)))))

(defn foo [{:keys [inputs input->grid-points]} grid-point]
  (let [input->distance (zipmap inputs (map #(distance grid-point %) inputs))
        distances (vals input->distance)]
    (if (unique-minimum? distances)
      (let [winner (key (apply min-key val input->distance))]
        {:inputs inputs
         :input->grid-points (update input->grid-points winner conj grid-point)})
      {:inputs inputs :input->grid-points input->grid-points})))

(defn calc-distances [from to inputs]
  (let [grid (for [x (range (:x from) (:x to))
                   y (range (:y from) (:y to))]
               (p x y))
        input->grid-points (:input->grid-points (reduce foo {:inputs inputs :input->grid-points {}} grid))]
    (map-values count input->grid-points)))

(defn -main []
  (let [inputs (map #(apply p %) (get-inputs))
        first (calc-distances (p 0 0) (p 400 400) inputs)
        second (calc-distances (p -200 -200) (p 600 600) inputs)]
    (filter #(= (val %) (second (key %))) first)))

