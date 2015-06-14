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

(defmacro order-queries [msg & body]
  (let [pairs (partition 2 body)
        fp (for [[f k] pairs]
             (list (list f msg) (list assoc msg :query k)))
        flat-fp (mapcat identity fp)]
    `(cond ~@flat-fp)))
