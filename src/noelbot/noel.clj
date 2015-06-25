(ns noelbot.noel
  (:require [noelbot.conf :as conf]
            [noelbot.db :as db]
            [noelbot.util :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clojure.string :as s]))

(defn chan-users [irc]
  (keys (get-in @irc [:channels (:chan conf/irc) :users])))

(defn no-query-found [msg]
  (format-line [:no-query] (:nick msg)))

(defn addressed-to-me? [text]
  (re-find #"(?i)(Noel|Noelbot)" text))

;; Query finding

(defn find-love [msg]
  (re-find #"(?i)I love you" (:text msg)))

(defn find-music-rec [msg]
  (re-find #"(?i)music" (:text msg)))

(defn find-artigas-time [msg]
  (re-find #"(?i)Artigas time" (:text msg)))

(defn find-horror [msg]
  (re-find #"(?i)Takemikazuchi|witch|kill|murder" (:text msg)))

(defn find-sad-panda [msg]
  (re-find #"(?i)porn|sad panda" (:text msg)))

(defn find-personal-query [msg]
  (order-queries msg
                 find-love :love
                 find-horror :horror
                 find-music-rec :music-rec
                 find-artigas-time :artigas-time
                 find-sad-panda :sadpanda))

;; Query answering

(defmulti answer-query :query)

(defmethod answer-query :love
  [query irc]
  (if (> (rand) 0.5)
    (format-line [:love-you] (:nick query))
    (format-line [:love-you-not] (rand-nth (chan-users irc)))))

(defmethod answer-query :music-rec
  [query _]
  (if-let [song (db/get-music-rec)]
    (format (get-line :music)
            (:nick query)
            (:title song)
            (:artist song)
            (:link song))
    (format-line [:no-music] (:nick query))))

;; uy-offset must be manually updated to compensate the DST
(defmethod answer-query :artigas-time
  [_ _]
  (let [uy-offset -3
        uy-formatter (tf/with-zone (tf/formatter "HH:mm")
                                   (t/time-zone-for-offset uy-offset))]
    (format-line [:artigas-time] (.print uy-formatter (t/now)))))

(defmethod answer-query :horror
  [query _]
  (format-line [:horror] (s/upper-case (:nick query))))

(defmethod answer-query :sadpanda
  [_ _]
  (if-let [link (db/get-sadpanda-rec)]
    (format-line [:sadpanda] link)
    (get-line :db-error)))
