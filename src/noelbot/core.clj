(ns noelbot.core
  (:require [noelbot.irc :as irc]
            [environ.core :refer [env]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (irc/start-irc))
