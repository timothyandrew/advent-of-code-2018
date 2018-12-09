(ns advent.5
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import java.time.format.DateTimeFormatter
           java.time.LocalDateTime
           java.time.temporal.ChronoUnit))

(def input (-> "input/5.txt"
               io/resource
               slurp
               (str/split #"")
               drop-last))

(defn units []
  (mapcat (fn [[a b]]
            [(str (char a) (char b))])
          (zipmap (range 97 123) (range 65 91))))

(defn make-partitions [input]
  (-> (partition 2 1 input)
      vec
      (conj `(~(last input) nil))))

(defn reaction [{:keys [reacted removed?]} [a b]]
  (cond
    removed? {:reacted reacted :removed? false}
    (nil? b) {:reacted (conj reacted a)}
    :else (if (or
               (and (= (str/upper-case a) b)
                    (= (str/lower-case b) a))
               (and (= (str/upper-case b) a)
                    (= (str/lower-case a) b)))
            {:reacted reacted :removed? true}
            {:reacted (conj reacted a) :removed? false})))

(defn react [input]
  (:reacted (reduce reaction {:reacted []} (make-partitions input))))

(defn react-until-stable [input]
  (let [output (react input)]
    (if (= output input)
      output
      (react-until-stable output))))

(defn remove-units-matching [input [l r]]
  (-> input
      (str/replace (re-pattern l) "")
      (str/replace (re-pattern r) "")))

(defn best-unit [input]
  (map (fn [unit]
         (let [unit (str/split unit #"")
               input (remove-units-matching (apply str input) unit)]
           [unit (count (react-until-stable (str/split input #"")))]))
       (units)))
