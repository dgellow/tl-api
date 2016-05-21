(ns remembrmoe.tl-api.handlers
  (:require [ring.util.response :as response]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :as r]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [clojure.data.json :as json]))

(defn success [body]
  (merge (r/response body)
         {:body {:ok true}}))

(defn error [body code description]
  (merge (r/response body)
         {:status 501
          :body {:ok false
                 :error-code code
                 :description description}}))

(defn not-found [body]
  (error body 404 "Method not found"))

(defroutes app-routes
  (GET "/" request
       (timbre/info request)
       (success {:result "hello"}))

  (fn [request]
    (timbre/info request)
    (not-found nil)))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response {:headers {"Content-Type" "application/json"}})))
