(ns remembrmoe.tl-api.test-scrap
  (:require [clojure.test :refer [deftest is]]
            [remembrmoe.tl-api.scrap :as scrapper]
            [net.cgrand.enlive-html :as html]))

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
  (let [line (first (scrapper/fetch-lines))
        page (scrapper/fetch-url (scrapper/make-direction-url line))]
    (is (not (nil? line)))
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-directions))))))

(deftest fetch-directions
  (let [line (first (scrapper/fetch-lines))
        directions (scrapper/fetch-directions line)]
    (is (not (nil? line)))
    (is (not (empty? directions)))
    (is (every? :id directions))
    (is (every? :direction directions))))

(deftest fetch-stations-page
  (let [line (first (scrapper/fetch-lines))
        direction (first (scrapper/fetch-directions line))
        page (scrapper/fetch-url (scrapper/make-station-url direction))]
    (is (not (nil? line)))
    (is (not (nil? direction)))
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-stations))))))

(deftest fetch-stations
  (let [line (first (scrapper/fetch-lines))
        direction (first (scrapper/fetch-directions line))
        stations (scrapper/fetch-stations direction)]
    (is (not (nil? line)))
    (is (not (nil? direction)))
    (is (not (empty? stations)))
    (is (every? :id stations))
    (is (every? :id-stop stations))))

(deftest fetch-horaire-page
  (let [line (first (scrapper/fetch-lines))
        direction (first (scrapper/fetch-directions line))
        station (first (scrapper/fetch-stations direction))
        page (scrapper/fetch-url
              (scrapper/make-horaire-url
               (:id line) (:id direction) (:id station)))]
    (is (not (nil? line)))
    (is (not (nil? direction)))
    (is (not (nil? station)))
    (is (not (empty? page)))
    (is (not (empty? (html/select page scrapper/selector-horaire))))))

(deftest fetch-horaire
  (let [line (first (scrapper/fetch-lines))
        direction (first (scrapper/fetch-directions line))
        station (first (scrapper/fetch-stations direction))
        horaire (scrapper/fetch-horaire
                 (:id line) (:id direction) (:id station))]
    (is (not (nil? line)))
    (is (not (nil? direction)))
    (is (not (nil? station)))
    (is (not (empty? horaire)))
    (is (every? #(not (clojure.string/blank? %)) horaire))))
