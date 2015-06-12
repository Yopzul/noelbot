(ns noelbot.noel
  (:require [noelbot.conf :as conf]
            [clojure.core.typed :as t]))

;; Utils

(defn get-line [& tags]
  (let [lines (get-in conf/comm tags)]
    (rand-nth lines)))

(defn format-line [tags fill-in]
  (let [line (apply get-line tags)]
    (if (re-find #"%s|%d" line)
      (format line fill-in)
      line)))

(defn chan-users [irc]
  (keys (get-in @irc [:channels (:chan conf/irc) :users])))

(defn no-query-found [msg]
  (format-line [:no-query] (:nick msg)))

(defn addressed-to-me? [text]
  (re-find #"(?i)(Noel|Noelbot)" text))

;; Query finding

(defn find-love [msg]
  (re-find #"(?i)I love you" (:text msg)))

(defn find-personal-query [msg]
  (cond
    (find-love msg) (assoc msg :query :love)))

;; Query answering

(defmulti answer-query :query)

(defmethod answer-query :love
  [query irc]
  (if (> (rand) 0.5)
    (format-line [:love-you] (:nick query))
    (format-line [:love-you-not] (rand-nth (chan-users irc)))))
