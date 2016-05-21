(set-env!
 :resource-paths #{"src"}
 :dependencies '[
                 ;; Backend
                 [http-kit "2.1.19"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.taoensso/timbre "4.3.1"]
                 [timbre-logstash "0.3.0"]

                 ;; Dev tools
                 [pandeiro/boot-http "0.7.3"]
                 [org.clojure/tools.namespace "0.2.11"]

                 ;; Clojure version
                 [org.clojure/clojure "1.8.0"]])

(task-options!
 pom {:project 'remembrmoe/tl-api
      :version "0.1.0-SNAPSHOT"}
 aot {:namespace #{'remembrmoe.tl-api.core}}
 jar {:main 'remembrmoe.tl-api.core})

(deftask build
  "Build project"
  []
  (comp (aot)
        (pom)
        (uber)
        (jar)))
