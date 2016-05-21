(ns remembrmoe.tl-api.core
  (:require [org.httpkit.server :as http-kit]
            [taoensso.timbre :as timbre]
            [active.timbre-logstash :as timbre-logstash]
            [remembrmoe.tl-api.state :refer [web-server]]
            [remembrmoe.tl-api.handlers :refer [app]])
  (:gen-class))

;; Logging
;; (timbre/merge-config!
;;  {:timestamp-opts {:pattern :iso8601}
;;   :appenders
;;   {:logstash
;;    (timbre-logstash/timbre-json-appender "localhost" 2500)}})

;; State machines
(defn parse-port [port]
  (when port
    (cond
      (string? port) (Integer/parseInt port)
      (number? port) port
      :else (throw (Exception. (str "Invalid port value: " port))))))

(defn http-port [port]
  (parse-port (or port 3000)))

(defn ^:private start-web-server-intern! [ring-handler port]
  (timbre/info "Starting web server (http-kit) ...")
  (let [http-kit-stop-fn (http-kit/run-server ring-handler {:port port})]
    {:server nil
     :port (:local-port (meta http-kit-stop-fn))
     :stop-fn (fn [] (http-kit-stop-fn :timeout 1000))}))

(defn stop-web-server! []
  (timbre/info "Stopping web-server (http-kit)...")
  (when-let [m @web-server] ((:stop-fn m))))

(defn start-web-server! [& [port]]
  (stop-web-server!)
  (let [{:keys [stop-fn port]
         :as server-map} (start-web-server-intern! (var app) port)
        uri (format "http://localhost:%s/" port)]
    (timbre/info "Web server is running at " uri)
    (reset! web-server server-map)))

(defn stop-app! []
  (stop-web-server!))

(defn start-app! [[port]]
  (let [port (http-port port)]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app!))
    (start-web-server! port)))

(defn -main [& args]
  (start-app! (or args [(System/getenv "PORT")])))
