(ns noelbot.db
  (:require [noelbot.conf :as conf]
            [noelbot.util :refer :all]
            [little-couch.core :as couch]))

(def db (couch/db-setup {:address  (:address conf/db)
                         :database (:database conf/db)}))

(defn get-music-rec []
  (let [recs (couch/where db {:type "music_rec"})]
    (when-not (zero? (count recs))
      (rand-nth recs))))