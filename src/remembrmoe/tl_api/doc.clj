(ns remembrmoe.tl-api.doc
  (:require [clojure.java.io :as io]
            [markdown2clj.core :as md]))

(defn read-documentation-file []
  (slurp (io/resource "documentation.md")))

(defprotocol Renderer
  (heading [_ segment])
  (paragraph [_ segment])
  (fenced-code-block [_ segment])
  (text [_ segment])
  (link [_ segment])
  (soft-line-break [_ segment])
  (code [_ segment])
  (bold [_ segment])
  (italic [_ segment]))

(defn render-segment [renderer md-segment]
  (cond
    (:paragraph md-segment)
    (paragraph renderer (:paragraph md-segment))

    (:heading md-segment)
    (heading renderer (:heading md-segment))

    (:fenced-code-block md-segment)
    (fenced-code-block renderer (:fenced-code-block md-segment))

    (:text md-segment)
    (text renderer (:text md-segment))

    (:link md-segment)
    (link renderer (:link md-segment))

    (:soft-line-break md-segment)
    (soft-line-break renderer (:soft-line-break md-segment))

    (:code md-segment)
    (code renderer (:code md-segment))

    (:bold md-segment)
    (bold renderer (:bold md-segment))

    (:italic md-segment)
    (italic renderer (:italic md-segment))

    :default
    (throw (java.lang.UnsupportedOperationException.
            (str
             "No rendering function has been implemented for this markdown segment:\n" md-segment)))))

(defrecord HtmlRenderer []
  Renderer
  (heading [renderer [{level :level} & more]]
    (->> more
       (map (partial render-segment renderer))
       clojure.string/join
       ((fn [x] (format "<h%s>%s</h%s>" level x level)))))
  (paragraph [renderer segment]
    (->> segment
       (map (partial render-segment renderer))
       clojure.string/join
       (format "<p>%s</p>")))
  (fenced-code-block [_ segment]
    (->> (some :text segment)
       ((fn [x] (format "<pre>%s</pre>" x)))))
  (text [_ text] text)
  (link [_ [{title :title} {dest :destination} {text :text}]]
    (str "<a"
         (format " href=\"%s\"" dest)
         (when title (format " title=\"%s\"" title))
         ">" text "</a>"))
  (soft-line-break [_ _] "\n")
  (code [_ [{text :text}]]
    (format "<code class=\"inline\">%s</code>" text))
  (bold [_ [{text :text}]]
    (format "<strong>%s</strong>" text))
  (italic [_ [{text :text}]]
    (format "<em>%s</em>" text)))

(defn indent
  ([n s] (indent n " " s))
  ([n char s]
   (str (clojure.string/join (repeat n char)) s)))

(defrecord AnsiRenderer []
  Renderer
  (heading [renderer [{level :level} & more]]
    (->> more
       (map (partial render-segment renderer))
       clojure.string/join
       (.toUpperCase)
       (indent (- level 2) "   ")
       ((fn [x] (bold renderer [{:text x}])))
       (str "\n")))
  (paragraph [renderer segment]
    (->> segment
       (map (partial render-segment renderer))
       clojure.string/join
       (partition-all 70)
       (map clojure.string/join)
       (map clojure.string/trim)
       (map (partial indent 6))
       (clojure.string/join "\n")
       (format "%s\n")))
  (fenced-code-block [_ segment]
    (->> (some :text segment)
       (indent 8)))
  (text [_ text] text)
  (link [_ [{title :title} {dest :destination} {text :text}]]
    (str "[4m" dest "[0m"))
  (soft-line-break [_ _] " ")
  (code [_ [{text :text}]]
    (format "`%s'" text))
  (bold [_ [{text :text}]]
    (format "[1m%s[0m" text))
  (italic [_ [{text :text}]]
    (format "[3m%s[0m" text)))

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

  pre {
    border: 1px solid #DADADA;
    margin-left: 80px;
    padding: 10px;
  }

  p.indent {
    padding-left: 140px;
  }

  code {
    background-color: #F1F1F1;
    padding: 3px;
    box-sizing: border-box;
    border-radius: 3px;
    color: #AD7000;
  }")

(defn to-html [doc]
  (->> doc
     (map (partial render-segment (HtmlRenderer.)))
     (clojure.string/join "\n")
     (#(str
        "<html><head><meta name=viewport content='width=device-width, initial-scale=1'></head>"
        "<style>" html-styles "</style>"
        "<body>" % "</body></html>\n"))))

(defn gen-html []
  (to-html (:document (md/parse (read-documentation-file)))))

(defn to-ansi [doc]
  (->> doc
     (map (partial render-segment (AnsiRenderer.)))
     (clojure.string/join "\n")
     (#(str % "\n"))))

(defn gen-ansi []
  (to-ansi (:document (md/parse (read-documentation-file)))))
