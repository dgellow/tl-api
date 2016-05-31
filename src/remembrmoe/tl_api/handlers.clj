(ns remembrmoe.tl-api.handlers
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.core :refer [defroutes GET context]]
            [taoensso.timbre :as timbre]
            [dgellow.utils.web-api :refer [success method-not-found]]
            [remembrmoe.tl-api.api-v1 :as v1]))

(defroutes app-routes
  (GET "/" request
       (timbre/info request)
       (success "hello"))

  (context "/api/v1" [] v1/routes)

  (fn [request]
    (timbre/info request)
    (method-not-found)))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response)))
