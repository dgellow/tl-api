(ns remembrmoe.tl-api.handlers
  (:require [net.cgrand.enlive-html :as html]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]))

(def test-url "http://www.t-l.ch/tl-live-mobile/line_detail.php?from=horaire&id=3377704015495675&line=11821953316814849&id_stop=2533279085546870&id_direction=11821953316814849&lineName=1")

(def list-lines-url "http://www.t-l.ch/tl-live-mobile/index.php")

(def line-direction-base-url
  "http://www.t-l.ch/tl-live-mobile/horaire_level2.php")
(defn make-line-direction-url [line-map]
  (str line-direction-base-url "?"
       "id=" (:id line-map) "&"
       "lineName=" (:name line-map)))

(def line-station-base-url
  "http://www.t-l.ch/tl-live-mobile/horaire_level3.php")
(defn make-line-station-url [direction-map]
  (str line-station-base-url "?"
       "id=" (:id direction-map) "&"
       "id_direction=" (:direction direction-map)))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def selector-waiting-time
  [[:ul (html/attr= :data-role "listview")] :.time])

(defn extract-waiting-time [page]
  (let [selection (html/select page selector-waiting-time)]
    (map (comp first :content) selection)))

(defn next-waiting-time [page]
  (first (extract-waiting-time page)))

(def selector-lines
  [[:ul (html/attr= :data-role "listview")] :a])

(defn extract-lines [page]
  (let [selection (html/select page selector-lines)]
    (->> selection
       ;; Get href attributes
       (map (comp
             :href
             :attrs))
       ;; Get url parameters
       (map (comp
             #(str/split % #"&")
             second
             #(str/split % #"\?")))
       ;; Convert to hashmap
       (map
         #(identity
           {:id (second (str/split (first %) #"="))
            :name (second (str/split (second %) #"="))})))))

(defn lines []
  (extract-lines (fetch-url list-line-url)))

(defn line-where [key value]
  (some #(and (= (key %) value) %) (lines)))

(defn line-by-name [name]
  (line-where :name name))

(defn line-by-id [id]
  (line-where :id id))

(def selector-directions
  [[:ul (html/attr= :data-role "listview")] :a])

(defn extract-directions [page]
  (let [selection (html/select page selector-directions)]
    (->> selection
       ;; Get href attributes
       (map (comp
             :href
             :attrs))
       ;; Get url parameters
       (map (comp
             #(str/split % #"&")
             second
             #(str/split % #"\?")))
       ;; Convert to hashmap
       (map
         #(identity
           {:id (second (str/split (first %) #"="))
            :direction (second (str/split (second %) #"="))})))))

(defn line-directions [line-map]
  (extract-directions (fetch-url (make-line-direction-url line-map))))

(def selector-stations
  [[:ul (html/attr= :data-role "listview")] :.arret :a])

(defn extract-stations [page]
  (let [selection (html/select page selector-stations)]
    (->> selection
       ;; Get href attributes
       (map (comp
             :href
             :attrs))
       ;; Get url parameters
       (map (comp
             #(str/split % #"&")
             second
             #(str/split % #"\?")))
       ;; Convert to hashmap
       (map
         #(identity
           {:id (second (str/split (second %) #"="))
            :id-stop (second (str/split (nth % 3) #"="))})))))

(defn direction-stations [direction-map]
  (extract-stations (fetch-url (make-line-station-url direction-map))))

(defn fetch-tl-state []
  (map (fn [line-map]
         (assoc line-map :directions
                (map
                  (fn [direction-map]
                    (assoc direction-map :stations
                           (direction-stations direction-map)))
                  (line-directions line-map))))
    (lines)))
