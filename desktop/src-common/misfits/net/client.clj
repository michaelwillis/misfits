(ns misfits.net.client
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all]))

(def tile-size 32)

(defn connect [server-host]
  (-> aleph-params 
      (assoc :host server-host)
      aleph/tcp-client
      lamina/wait-for-result
      ednify-channel))

(defn notify [channel topic message]
  (lamina/enqueue channel [topic message]))

(defn remove-all-tiles [entities]
  (filter (fn [e] (not= (:layer e) :tile)) entities))

(defn create-tile-sprite-entity [x y sprite]
  (assoc sprite :layer :tile :x (* tile-size x) :y (* tile-size y)))

(defn tile-sprites-at-location [sprites floor-map x y]
  (let [this-tile (floor-map [x y])
        down-tile (floor-map [x (dec y)])
        tile-sprites [(cond (= this-tile :floor) (sprites :stone1)
                       (= this-tile :wall) (if (= down-tile :wall) 
                                             (sprites :wall-top) 
                                             (sprites :wall-front))
                       (= this-tile :pit) (sprites :pit))]
        tile-sprites (if (and (not= this-tile :wall) (= down-tile :wall))
                       (conj tile-sprites (sprites :wall-back))
                       tile-sprites)]
    (map (partial create-tile-sprite-entity x y) tile-sprites)))

(defn place-new-tiles [sprites floor-map entities]
  (reduce concat entities (for [x (range 0 16) y (range 0 16)]
                            (tile-sprites-at-location sprites floor-map x y))))

(def decode-tile
  {\O :pit
   \H :wall
   \_ :floor})

(defn handle-message [sprites entities topic message]
  (println (str "got message! " topic ", " message))
  (let [new-floor-map (into {} (for [x (range 0 16) y (range 0 16)]
                                 [[x y] (decode-tile (-> (:floor message) (nth x) (nth y)))]))]
    (->> entities remove-all-tiles 
         (place-new-tiles sprites new-floor-map))))

(defn handle-server-messages [client-channel sprites entities]
  ;(println (str "server messages! " client-channel ", " (type client-channel)))
;  (println (count client-channel))
  (if (= 0 (count client-channel)) entities
      (let [[topic message] @(lamina/read-channel client-channel)
            entities (handle-message sprites entities topic message)]
        (recur client-channel sprites entities))))


(comment
  (if (= 0 (count client-channel)) entities
      (let [entities (handle-message sprites entities (lamina/read-channel client-channel))]
        (recur client-channel sprites entities))))
