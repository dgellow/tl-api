(ns remembrmoe.tl-api.handlers
  (:require [ring.util.response :as response]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :as r]
            [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [clojure.data.json :as json]
            [remembrmoe.tl-api.state :as state]
            [remembrmoe.tl-api.scrap :as scrapper]
            [remembrmoe.tl-api.query :as q]))

(defn success [body]
  (merge (r/response body)
         {:body {:ok true
                 :result body}
          :headers {"Content-Type" "application/json"}}))

(defn error [code description]
  (merge (r/response nil)
         {:status 501
          :body {:ok false
                 :error-code code
                 :description description}
          :headers {"Content-Type" "application/json"}}))

(defn method-not-found []
  (error 404 "Method not found"))

(defn resource-not-found []
  (error 404 "Resource not found"))

(defroutes app-routes
  (GET "/" request
       (timbre/info request)
       (success "hello"))

  (context
   "/api/v1" []
   (GET "/refresh" request
        (timbre/info request)
        (reset! state/tl-state (scrapper/fetch-tl-state))
        (success @state/tl-state))

   (GET "/state" request
        (timbre/info request)
        (success @state/tl-state))

   (GET "/lines" request
        (timbre/info request)
        (success (q/get-lines @state/tl-state)))

  (GET "/lines/:id-or-name" request
        (timbre/info request)
        (let [id-or-name (get-in request [:params :id-or-name])
              line (q/line-by-name-or-id @state/tl-state id-or-name)]
          (if (seq line)
            (success line)
            (resource-not-found ))))

  (GET "/lines/:id-or-name/directions" request
       (timbre/info request)
       (let [id-or-name (get-in request [:params :id-or-name])
             directions (q/get-directions @state/tl-state id-or-name)]
         (if (seq (:directions directions))
            (success directions)
            (resource-not-found ))))

  (GET "/lines/:id-or-name/directions/:direction-id/stations" request
       (timbre/info request)
       (let [id-or-name (get-in request [:params :id-or-name])
             direction-id (get-in request [:params :direction-id])
             stations (q/get-stations @state/tl-state id-or-name
                                      direction-id)]
         (if (seq (:stations stations))
            (success stations)
            (resource-not-found )))))

  (fn [request]
    (timbre/info request)
    (method-not-found)))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response)))
