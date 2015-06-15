(ns noelbot.script
  (:require [noelbot.conf :as conf]
            [noelbot.db :as db]
            [little-couch.core :as couch])
  (:import (java.util UUID)))

;; Functions used to set up the environment, notably the database

(def dev-conf (read-string (slurp "conf/dev.edn")))
(def prod-conf (read-string (slurp "conf/prod.edn")))

(defn create-dbs []
  (if (:ok (couch/create (couch/db-setup (:db dev-conf))))
    "Created the dev db" "Failed to create the dev db")
  (couch/create (couch/db-setup (:db prod-conf))))

(defn fill-music-rec []
  (let [songs (read-string (slurp "music_rec.edn"))
        db (couch/db-setup (:db prod-conf))]
    (doseq [song songs]
      (couch/create-doc db
                        (UUID/randomUUID)
                        (merge song {:type "music_rec"})))))

(defn setup-dbs []
  (create-dbs)
  (fill-music-rec))

;; Call this function to set everything up
(defn setup []
  (setup-dbs))
