(ns remembrmoe.tl-api.test-scrap
  (:require [clojure.test :refer [deftest is]]
            [remembrmoe.tl-api.scrap :as scrapper]
            [taoensso.timbre :as timbre]
            [net.cgrand.enlive-html :as html]))

(defn select-item [coll]
  (rand-nth coll))

(def line (select-item (scrapper/fetch-lines)))
(deftest selected-line
  (is (not (nil? line))))

(def direction (select-item (scrapper/fetch-directions line)))
(deftest selected-direction
  (is (not (nil? direction))))

(def station (select-item (scrapper/fetch-stations direction)))
(deftest selected-station
  (is (not (nil? station))))

(timbre/info (str "Random Pick\n"
                  {:line line
                   :direction direction
                   :station station}))

(deftest fetch-tl-live-root-url
  (let [page (scrapper/fetch-url
              "http://www.t-l.ch/tl-live-mobile/index.php")]
    (is (not (empty? page)))))

(deftest fetch-lines-page
  (let [page (scrapper/fetch-url scrapper/lines-url)]
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-lines))))))

(deftest fetch-lines
  (let [lines (scrapper/fetch-lines)]
    (is (not (empty? lines)))
    (is (every? :id lines))
    (is (every? :name lines))))

(deftest fetch-directions-page
  (let [page (scrapper/fetch-url (scrapper/make-direction-url line))]
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-directions))))))

(deftest fetch-directions
  (let [directions (scrapper/fetch-directions line)]
    (is (not (empty? directions)))
    (is (every? :id directions))
    (is (every? :direction directions))))

(deftest fetch-stations-page
  (let [page (scrapper/fetch-url (scrapper/make-station-url direction))]
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-stations))))))

(deftest fetch-stations
  (let [stations (scrapper/fetch-stations direction)]
    (is (not (empty? stations)))
    (is (every? :id stations))
    (is (every? :id-stop stations))))

(deftest fetch-horaires-page
  (let [page (scrapper/fetch-url
              (scrapper/make-horaires-url
               (:id line) (:id direction) (:id station)))]
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-horaires))))))

(deftest fetch-horaires
  (let [horaires (scrapper/fetch-horaires
                 (:id line) (:id direction) (:id station))]
    (is (not (empty? horaires)))
    (is (every? #(not (clojure.string/blank? %)) horaires))))
