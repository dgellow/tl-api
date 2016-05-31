(ns remembrmoe.tl-api.api-v1
  (:require [compojure.core :refer [defroutes GET context]]
            [taoensso.timbre :as timbre]
            [dgellow.utils.web-api :refer [success resource-not-found]]
            [remembrmoe.tl-api.state :as state]
            [remembrmoe.tl-api.scrap :as scrapper]
            [remembrmoe.tl-api.query :as q]))

(def refresh
  (GET "/refresh" request
       (timbre/info request)
       (reset! state/tl-state (scrapper/fetch-tl-state))
       (success @state/tl-state)))

(def get-state
  (GET "/state" request
         (timbre/info request)
         (success @state/tl-state)))

(def get-lines
  (GET "/lines" request
       (timbre/info request)
       (success (q/get-lines @state/tl-state))))

(def get-line
  (GET "/lines/:id-or-name" request
       (timbre/info request)
       (let [id-or-name (get-in request [:params :id-or-name])
             line (q/line-by-name-or-id @state/tl-state id-or-name)]
         (if (seq line)
           (success line)
           (resource-not-found )))))

(def get-directions
  (GET "/lines/:id-or-name/directions" request
       (timbre/info request)
       (let [id-or-name (get-in request [:params :id-or-name])
             directions (q/get-directions @state/tl-state id-or-name)]
         (if (seq (:directions directions))
           (success directions)
           (resource-not-found )))))

(def get-stations
  (GET "/lines/:id-or-name/directions/:direction-id/stations" request
       (timbre/info request)
       (let [id-or-name (get-in request [:params :id-or-name])
             direction-id (get-in request [:params :direction-id])
             stations (q/get-stations @state/tl-state id-or-name
                                      direction-id)]
         (if (seq (:stations stations))
           (success stations)
           (resource-not-found )))))

(def get-horaires
  (GET "/lines/:line-id/directions/:direction-id/stations/:station-id/horaires"
       request
       (timbre/info request)
       (let [line-id (get-in request [:params :line-id])
             direction-id (get-in request [:params :direction-id])
             station-id (get-in request [:params :station-id])
             horaires (scrapper/fetch-horaires
                      line-id direction-id station-id )]
         (if (seq horaires)
           (success horaires)
           (resource-not-found )))))

(defroutes routes
  refresh
  get-state
  get-lines
  get-line
  get-directions
  get-stations
  get-horaires)
