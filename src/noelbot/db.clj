(ns noelbot.db
  (:require [noelbot.conf :as conf]
            [noelbot.util :refer :all]
            [little-couch.core :as couch])
  (:import (java.util UUID)))

(def db (couch/db-setup {:address  (:address conf/db)
                         :database (:database conf/db)}))

(defn get-music-rec []
  (let [recs (couch/where db {:type "music_rec"})]
    (when-not (zero? (count recs))
      (rand-nth recs))))

(defn add-music-rec [doc]
  (couch/create-doc db
                    (UUID/randomUUID)
                    (merge doc {:type "music_rec"})))