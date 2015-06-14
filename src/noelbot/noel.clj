(ns noelbot.noel
  (:require [noelbot.conf :as conf]
            [noelbot.db :as db]
            [noelbot.util :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as tf]))

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

(defn find-personal-query [msg]
  (order-queries msg
    find-love :love
    find-music-rec :music-req
    find-artigas-time :artigas-time))

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
