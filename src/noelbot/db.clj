(ns noelbot.db
  (:require [noelbot.conf :as conf]
            [noelbot.util :refer :all]
            [little-couch.core :as couch]))

(def db (couch/db-setup {:address  (:address conf/db)
                         :database (:database conf/db)}))

(defn get-random-doc [database design view-name]
  (try
      (let [view (partial couch/view database design view-name)
            count (-> (view) :rows first :value)
            rng (rand)
            index (int (* count rng))
            lasting (-> (view {:end_key rng}) :rows first :value)
            skip (- index lasting)]
        (-> (view (if (>= skip 0)
                    {:start_key rng :skip skip :limit 1 :reduce false}
                    {:start_key rng :skip (- (inc skip)) :limit 1 :reduce false
                     :descending true}))
            :rows first :value))
      (catch Exception e
        (println (.getMessage e)))))

(defn get-music-rec
  ([]
    (get-music-rec db))
  ([database]
   (get-random-doc database "_design/music_rec" "random")))

(defn add-music-rec
  ([doc]
    (add-music-rec db doc))
  ([database {:keys [artist title] :as doc}]
   (try
     (let [id (hash [artist title])]
       (couch/update-doc database
                         id
                         (merge doc {:type "music_rec" :rand (rand)})))
     (catch Exception e
       (println (.getMessage e))))))

(defn get-sadpanda-rec
  ([]
    (get-sadpanda-rec db))
  ([database]
    (get-random-doc database "_design/sadpanda_rec" "random")))

(defn add-sadpanda-rec
  ([doc]
    (add-sadpanda-rec db doc))
  ([database {:keys [link] :as doc}]
    (try
      (let [id (hash link)]
        (couch/update-doc database
                          id
                          (merge doc {:type "sadpanda_rec" :rand (rand)})))
      (catch Exception e
        (println (.getMessage e))))))
