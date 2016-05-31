(ns remembrmoe.tl-api.doc
  (:require [clj-yaml.core :as yaml]))

(defn read-documentation-file []
  (slurp "res/documentation.yaml"))

(defprotocol Formatter
  (bold [x s])
  (title [x s])
  (title2 [x s])
  (p [x s])
  (p2 [x s]))

(defrecord HtmlFormatter []
  Formatter
  (bold [_ s] (str "<b>" s "</b>"))
  (title [_ s] (str "<h2>" s "</h2>"))
  (title2 [_ s] (str "<h3>" s "</h3>"))
  (p [_ s] (str "<p>" s "</p>"))
  (p2 [_ s] (str "<p class='indent'>" s "</p>")))

(defrecord AnsiFormatter []
  Formatter
  (bold [_ s] (str "[1m" s "[0m"))
  (title [x s] (bold x (.toUpperCase s)))
  (title2 [x s] (p x (bold x (.toUpperCase s))))
  (p [_ s] (str (apply str (repeat 6 " ")) s))
  (p2 [_ s] (str (apply str (repeat 12 " ")) s)))

(def documentation
  (let [docs
        (map yaml/parse-string
          (clojure.string/split (read-documentation-file) #"---"))
        doc-header (first docs)
        doc-body (second docs)]
    (list doc-header doc-body)))

(defmulti format-content (fn [formatter x] (type x)))

(defmethod format-content java.lang.String [formatter x]
  (->> x
     (clojure.string/split-lines)
     (map (partial p formatter))
     (clojure.string/join "\n")))

(defmethod format-content flatland.ordered.map.OrderedMap [formatter x]
  (->> x
     (map (fn [[t c]]
            (list
             (->> (name t)
                (title2 formatter))
             (->> c
                (clojure.string/split-lines)
                (map (partial p2 formatter))
                (clojure.string/join "\n")))))))

(defn format-doc-section [formatter yml-object]
  (list (title formatter (:title yml-object))
        (format-content formatter (:content yml-object))
        "\n"))

(def html-styles
  "body {
    padding-right: 20px;
    padding-left: 20px;
    font-family: monospace;
    max-width: 800px;
    margin: auto;
    padding-top: 20px;
    padding-bottom: 40px;
  }

  h3 {
    padding-left: 60px;
  }

  p {
    padding-left: 80px;
  }

  p.indent {
    padding-left: 140px;
  }")

(defn to-html []
  (let [header (first documentation)
        body (map (partial format-doc-section (HtmlFormatter.))
               (second documentation))]
    (->> (list (str "<style>" html-styles "</style>")
             header body)
       flatten
       (clojure.string/join "\n")
       str
       (#(str "<html><body>" % "</body></html>\n")))))

(defn to-ansi []
  (let [header (first documentation)
        body (map (partial format-doc-section (AnsiFormatter.))
               (second documentation))]
    (->> (list header "" body)
       flatten
       (clojure.string/join "\n")
       str
       (#(str % "\n")))))
