(ns remembrmoe.tl-api.test-core
  (:require [clojure.test :refer [deftest is run-all-tests successful?]]
            [remembrmoe.tl-api.test-scrap])
  (:gen-class))

(defn -main [& args]
  (when (not (successful? (run-all-tests #"remembrmoe.tl-api.*")))
    (System/exit 1)))
