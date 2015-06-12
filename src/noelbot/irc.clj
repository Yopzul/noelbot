(ns noelbot.irc
  (:require [noelbot.conf :as conf]
            [noelbot.noel :as noel]
            [irclj.core :as irc]))

(defn answer [irc msg]
  (when (noel/addressed-to-me? (:text msg))
    (if-let [query (noel/find-personal-query msg)]
      (irc/message irc (:target msg)
                   (noel/answer-query query irc))
      (irc/message irc (:target msg)
                   (noel/no-query-found msg)))))

(defn start-irc []
  (let [connection (irc/connect
                     (:server conf/irc)
                     (:port conf/irc)
                     (:username conf/irc)
                     :callbacks {:privmsg answer})]
    (irc/join connection (:chan conf/irc))))
