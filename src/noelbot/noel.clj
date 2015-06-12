(ns noelbot.noel
  (:require [noelbot.conf :as conf]
            [noelbot.db :as db]
            [noelbot.util :refer :all]))

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
  (and (re-find #"(?i)music" (:text msg))
       (re-find #"(?i)recommend|give" (:text msg))))

(defn find-personal-query [msg]
  (cond
    (find-love msg) (assoc msg :query :love)
    (find-music-rec msg) (assoc msg :query :music-rec)))

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
