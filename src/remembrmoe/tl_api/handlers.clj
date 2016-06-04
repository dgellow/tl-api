(ns remembrmoe.tl-api.handlers
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.core :refer [defroutes GET context]]
            [taoensso.timbre :as timbre]
            [dgellow.utils.web-api :refer [success method-not-found]]
            [remembrmoe.tl-api.api-v1 :as v1]
            [remembrmoe.tl-api.doc :as doc]))

(defroutes app-routes
  (GET "/" request
       (timbre/info request)
       (if (.contains (get-in request [:headers "user-agent"]) "curl")
         {:headers {"Content-Type" "text/plain; charset=utf-8"}
          :body(doc/gen-ansi)}
         {:headers {"Content-Type" "text/html; charset=utf-8"}
          :body(doc/gen-html)}))

  (context "/api/v1" [] v1/routes)

  (fn [request]
    (timbre/info request)
    (method-not-found)))

(def app
  (-> app-routes
     (wrap-defaults api-defaults)
     (wrap-json-response)))
