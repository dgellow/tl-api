(ns remembrmoe.tl-api.handlers
  (:require [ring.util.response :as response]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :as r]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [clojure.data.json :as json]
            [remembrmoe.tl-api.state :as state]))

(defn success [body]
  (merge (r/response body)
         {:body {:ok true
                 :result body}}))

(defn error [code description]
  (merge (r/response nil)
         {:status 501
          :body {:ok false
                 :error-code code
                 :description description}}))

(defn not-found []
  (error 404 "Method not found"))

(defroutes app-routes
  (GET "/" request
       (timbre/info request)
       (success "hello"))

  (GET "/state" request
       (timbre/info request)
       (success @state/tl-state))

  (fn [request]
    (timbre/info request)
    (not-found)))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response {:headers {"Content-Type" "application/json"}})))
