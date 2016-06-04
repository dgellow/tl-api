(ns remembrmoe.tl-api.query
  (:require [taoensso.timbre :as timbre]))

(defn line-where [tl-state key value]
  (some #(and (= (key %) value) %) tl-state))

(defn line-by-name [tl-state name]
  (line-where tl-state :name name))

(defn line-by-id [tl-state id]
  (line-where tl-state :id id))

(defn line-by-name-or-id [tl-state line-name-or-id]
  (let [id? (and (every? #(try
                       (Integer/parseInt (str %))
                       (catch NumberFormatException e
                         false))
                       line-name-or-id)
               (> (count line-name-or-id) 10))]
    (if id?
      (line-by-id tl-state line-name-or-id)
      (line-by-name tl-state line-name-or-id))))

(defn get-lines [tl-state]
  (map #(select-keys % [:id :name :terminus :issue])
    tl-state))

(defn get-directions [tl-state line-name-or-id]
  (let [line (line-by-name-or-id tl-state line-name-or-id)]
    {:id-line (:id line)
     :name-line (:name line)
     :directions (map #(select-keys % [:id :direction])
                   (:directions line))}))

(defn get-stations [tl-state line-name-or-id direction-id]
  (let [line (line-by-name-or-id tl-state line-name-or-id)
        direction (some #(and (= (:id %) direction-id) %)
                        (:directions line))]
    {:id-line (:id line)
     :name-line (:name line)
     :id-direction (:id direction)
     :stations (:stations direction)}))
