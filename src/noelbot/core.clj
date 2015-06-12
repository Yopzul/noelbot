(ns noelbot.core
  (:require [noelbot.irc :as irc])
  (:gen-class))

(defn -main
  [& args]
  (irc/start-irc))
