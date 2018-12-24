(ns advent.7
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def file (-> "input/7.txt"
              io/resource
              slurp
              (str/split #"\n")))

(defn get-input []
  (map 
   #(drop 1 (re-matches #"Step (\w) must be finished before step (\w) can begin." %))
   file))

(defn build-initial-state [reqs]
  (reduce
   (fn [{:keys [req->steps step->reqs available]} [req step]]
     {:req->steps (update req->steps req conj step)
      :step->reqs (update step->reqs step conj req)
      :available available})
   {:available (set/difference (set (map first reqs)) (set (map second reqs)))}
   reqs))

(defn reqs-satisfied? [{:keys [step->reqs done]} step]
  (let [reqs (step->reqs step)]
    (every? #(contains? done %) reqs)))

(defn first-available [{:keys [req->steps step->reqs available done] :as state}]
  (->> available
       sort
       (filter #(reqs-satisfied? state %))
       first))

(defn tick [{:keys [req->steps step->reqs available done order] :as state}]
  (let [step (first-available state)]
    {:done (conj (or done #{}) step)
     :order (conj (or order []) step)
     :req->steps req->steps
     :step->reqs step->reqs
     :available (-> available
                    (set/union (set (req->steps step)))
                    (disj step))}))

(defn mainloop [reqs]
  (loop [state (build-initial-state reqs)]
    (if (empty? (:available state))
      state
      (recur (tick state)))))

(defn main-1 []
  (-> (get-input)
      mainloop
      :order
      str/join))
