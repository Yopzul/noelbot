(ns noelbot.script
  (:require [noelbot.db :as db]
            [clojure.java.io :as io]
            [little-couch.core :as couch]))

;; Functions used to set up the environment, notably the database

(def dev-conf (read-string (slurp "conf/dev.edn")))
(def prod-conf (read-string (slurp "conf/prod.edn")))

(defn create-dbs []
  (couch/create (couch/db-setup (:db dev-conf)))
  (couch/create (couch/db-setup (:db prod-conf))))

(defn fill-music-rec [database filename]
  (let [songs (read-string (slurp filename))]
    (doseq [s songs]
      (db/add-music-rec database s))))

(defn fill-sadpanda-rec [database filename]
  (with-open [f (io/reader filename)]
    (doseq [line (line-seq f)]
      (when (re-find #"^http://" line)
        (db/add-sadpanda-rec database {:link line})))))

(defn setup-prod-db []
  (let [db (couch/db-setup (:db prod-conf))]
    (couch/create db)
    (couch/create-doc db "_design/music_rec"
                      {:language "javascript"
                       :views {:random {:map "function(doc){if(doc.type==\"music_rec\"){emit(doc.rand,{artist:doc.artist,title:doc.title,link:doc.link});}}"
                                        :reduce "_count"}}})
    (couch/create-doc db "_design/sadpanda_rec"
                      {:language "javascript"
                       :views {:random {:map "function(doc){if(doc.type==\"sadpanda_rec\"){emit(doc.rand,{artist:doc.artist,title:doc.title,link:doc.link});}}"
                                        :reduce "_count"}}})))

;; Call this function to set everything up
(defn setup []
  (setup-prod-db))
