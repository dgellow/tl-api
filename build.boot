(set-env!
 :source-paths #{"src"}
 :resource-paths #{"res"}
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
                 [enlive "1.1.6"]
                 [dgellow/utils "0.1.0"]
                 [circleci/clj-yaml "0.5.5"]
                 [markdown2clj "0.1.3"]

                 ;; Dev tools
                 [adzerk/boot-test "1.1.1"]

                 ;; Clojure version
                 [org.clojure/clojure "1.8.0"]])

(require '[adzerk.boot-test :refer :all])

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

(deftask build-tests
  "Build project tests"
  []
  (set-env!
   :source-paths #{"src" "test"})
  (task-options!
   pom {:project 'remembrmoe/tl-api-tests
        :version "0.1.0-SNAPSHOT"}
   aot {:namespace #{'remembrmoe.tl-api.test-core}}
   jar {:main 'remembrmoe.tl-api.test-core})
  (comp (aot)
        (pom)
        (uber)
        (jar)))
