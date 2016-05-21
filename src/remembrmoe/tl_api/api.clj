(ns remembrmoe.tl-api.api
  (:require [taoensso.timbre :as timbre]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def api-root "https://api.telegram.org/bot")

(defn make-url [token method]
  (format "%s%s/%s" api-root token (str (name method))))

(defrecord Bot [token])

;; Updates
(defn get-updates [{:keys [token]}]
  (http/get (make-url token :getupdates)))

(defn set-webhook
  [{:keys [token]} url]
  (http/post
   (make-url token :setwebhook)
   {:form-params {:url url}}))

(defn del-webhook
  [{:keys [token]}]
  (http/post
   (make-url token :setwebhook)))

;; Methods
(defn get-me [{:keys [token]}]
  (http/get (make-url token :getme)))

(defn send-message
  ([bot chat-id message]
   (send-message bot chat-id message nil))
  ([{:keys [token] :as bot} chat-id message opts]
   (let [{:keys [parse-mode
                 disable-web-page-preview
                 disable-notification
                 reply-to-message-id
                 reply-markup] :as params} opts]
     (http/post
      (make-url token :sendmessage)
      {:form-params
       {:chat_id chat-id
        :text message
        :parse_mode parse-mode
        :disable_web_page_preview disable-web-page-preview
        :disable_notification disable-notification
        :reply_to_message_id reply-to-message-id
        :reply_markup reply-markup}}))))

(defn send-markdown
  ([bot chat-id message]
   (send-markdown bot chat-id message nil))
  ([bot chat-id message opts]
   (send-message bot chat-id message (assoc opts :parse-mode "markdown"))))

(defn forward-message
  ([bot chat-id from-chat-id message-id]
   (forward-message bot chat-id from-chat-id message-id nil))
  ([{:keys [token] :as bot} chat-id from-chat-id message-id opts]
   (let [{:keys [disable-notification] :as params} opts]
     (http/post
      (make-url token :forwardmessage)
      {:form-params
       {:chat_id chat-id
        :from_chat_id from-chat-id
        :message_id message-id
        :disable_notification disable-notification}}))))

(defn send-chat-action
  [{:keys [token] :as bot} chat-id action]
  (http/post
   (make-url token :sendchataction)
   {:form-params
    {:chat_id chat-id
     :action action}}))

;; Inline mode
(defn answer-inline-query
  ([bot inline-query-id result]
   (answer-inline-query bot inline-query-id result nil))
  ([{:keys [token] :as bot} inline-query-id results opts]
   (let [{:keys [cache-time
                 is-personal
                 next-offset
                 switch-pm-text
                 switch-pm-parameter] :as params} opts]
     (http/post
      (make-url token :answerinlinequery)
      {:form-params
       {:inline_query_id inline-query-id
        :results results
        :cache_time cache-time
        :is_personal is-personal
        :next_offset next-offset
        :switch_pm_text switch-pm-text
        :switch_pm_parameter switch-pm-parameter}}))))
