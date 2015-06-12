(ns noelbot.util
  (:require [noelbot.conf :as conf]))

(defn get-line [& tags]
  (let [lines (get-in conf/comm tags)]
    (rand-nth lines)))

(defn format-line [tags fill-in]
  (let [line (apply get-line tags)]
    (if (re-find #"%s|%d" line)
      (format line fill-in)
      line)))
