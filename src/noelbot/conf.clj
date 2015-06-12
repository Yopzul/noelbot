(ns noelbot.conf
  (:require [environ.core :refer [env]]))

(def conf (read-string (slurp (str "conf/" (env :conf)))))

(def irc (:irc conf))
(def comm (:comm conf))