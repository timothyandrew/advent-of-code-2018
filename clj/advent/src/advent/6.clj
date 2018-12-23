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
  (->>
   (map #(map (comp ->int str/trim) (str/split % #",")) input)
   (map #(apply p %))))

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

(defn foo [{:keys [inputs input->locations]} location]
  (let [input->distance (zipmap inputs (map #(distance location %) inputs))
        distances (vals input->distance)]
    (if (unique-minimum? distances)
      (let [winner (key (apply min-key val input->distance))]
        {:inputs inputs
         :input->locations (update input->locations winner conj location)})
      {:inputs inputs :input->locations input->locations})))

(defn calc-distances [from to inputs]
  (let [grid (for [x (range (:x from) (:x to))
                   y (range (:y from) (:y to))]
               (p x y))
        input->locations (:input->locations (reduce foo {:inputs inputs :input->locations {}} grid))]
    (map-values count input->locations)))

(defn -main []
  (let [inputs (get-inputs)
        first (calc-distances (p 0 0) (p 400 400) inputs)
        second (calc-distances (p -200 -200) (p 600 600) inputs)]
    (filter #(= (val %) (second (key %))) first)))

(defn stats []
  (let [inputs (get-inputs)]
    {:max-x (apply max-key :x inputs)
     :max-y (apply max-key :y inputs)
     :min-x (apply min-key :x inputs)
     :min-y (apply min-key :y inputs)}))

(defn find-region [n]
  (let [inputs (get-inputs)
        grid (for [x (range (- n) n)
                   y (range (- n) n)]
               (p x y))
        calc (fn [location]
               (reduce #(+ %1 (distance location %2)) 0 inputs))]
    (->> (pmap calc grid)
         (filter #(< % 10000))
         count)))

(defn -main-2 []
  (loop [n 300 prev nil]
    (println "Trying n = " n)
    (let [curr (find-region n)]
      (if (and prev (= curr prev))
        curr
        (recur (+ n 100) curr)))))
