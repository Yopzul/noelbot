(ns noelbot.irc
  (:require [noelbot.conf :as conf]
            [noelbot.noel :as noel]
            [irclj.core :as irc]))

(defn answer [irc msg]
  (let [target (if (= (:target msg) (:username conf/irc))
                 (:nick msg)
                 (:target msg))]
    (if (or (noel/addressed-to-me? (:text msg))
            (= (:target msg) (:username conf/irc)))
      (irc/message irc target
                   (if-let [query (noel/find-personal-query msg)]
                     (noel/answer-query query irc)
                     (noel/no-query-found msg))))))

(defn start-irc []
  (let [connection (irc/connect
                     (:server conf/irc)
                     (:port conf/irc)
                     (:username conf/irc)
                     :callbacks {:privmsg answer})]
    (irc/join connection (:chan conf/irc))))
