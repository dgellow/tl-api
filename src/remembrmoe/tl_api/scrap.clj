(ns remembrmoe.tl-api.scrap
  (:require [net.cgrand.enlive-html :as html]
            [taoensso.timbre :as timbre]
            [clojure.string :as str]))

(def lines-url "http://www.t-l.ch/tl-live-mobile/index.php")

(def direction-base-url
  "http://www.t-l.ch/tl-live-mobile/horaire_level2.php")
(defn make-direction-url [line-map]
  (str direction-base-url "?"
       "id=" (:id line-map) "&"
       "lineName=" (:name line-map)))

(def station-base-url
  "http://www.t-l.ch/tl-live-mobile/horaire_level3.php")
(defn make-station-url [direction-map]
  (str station-base-url "?"
       "id=" (:id direction-map) "&"
       "id_direction=" (:direction direction-map)))

(def horaires-base-url
  "http://www.t-l.ch/tl-live-mobile/line_detail.php")
(defn make-horaires-url [id-line id-direction id-station]
  (str horaires-base-url "?"
       "from=horaire" "&"
       "id=" id-station "&"
       "id_direction=" id-direction "&"
       "line=" id-line))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def selector-horaires
  [[:ul (html/attr= :data-role "listview")] :.time])

(defn extract-horaires [page]
  (let [selection (html/select page selector-horaires)]
    (->> selection
       (map :content)
       (map (fn [x]
              (if (some (fn [y] (get-in y [:attrs :src])) x)
                (str "~" (last x))
                (first x)))))))

(defn fetch-horaires [id-line id-direction id-station]
  (extract-horaires (fetch-url
                    (make-horaires-url id-line id-direction id-station))))

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

(defn fetch-lines []
  (extract-lines (fetch-url lines-url)))

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

(defn fetch-directions [line-map]
  (extract-directions (fetch-url (make-direction-url line-map))))

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

(defn fetch-stations [direction-map]
  (extract-stations (fetch-url (make-station-url direction-map))))

(defn fetch-tl-state []
  (map (fn [line-map]
         (assoc line-map :directions
                (map
                  (fn [direction-map]
                    (assoc direction-map :stations
                           (fetch-stations direction-map)))
                  (fetch-directions line-map))))
    (fetch-lines)))
