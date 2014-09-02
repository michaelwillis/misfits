(ns misfits.screens.main
  (:require [lamina.core :as lamina]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]
            [misfits.tiles :refer :all]))

(def tile-size 32)  ; in pixels

;; Yay side effects!
(def channel-atom (atom nil))
(def tile-sprites-atom (atom nil))
(def tile-layout-atom (atom []))
(def entities-atom (atom {})) ; game entities, not clj-play entities

(defn tile-sprite [y x tile]
  (if-let [sprite (@tile-sprites-atom tile)]
    (assoc sprite :layer :tile :x (-> x (* tile-size) (- 32)) :y (-> y (* tile-size) (- 32)))))

(defn organize-sprites []
  (->> @tile-layout-atom
       (map-indexed #(map-indexed (partial tile-sprite %1) %2))
       (apply concat)
       reverse))

(defn handle-server-messages [ch]
  (when (and ch (< 0 (count ch)))
    (let [[topic message] @(lamina/read-channel ch)]
      (case topic
        :floor (reset! tile-layout-atom (message :floor))
        (println (str "got unknown message! " topic ", " message))))
    (recur ch)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [camera (orthographic)
          screen (update! screen :camera camera :renderer (stage))
          tiles-sheet (texture "LPC_Tile_Atlas/tiles.png")
          tiles-textures (texture! tiles-sheet :split 64 64)
          tile (fn [x y] (texture (aget tiles-textures x y)))]

      (reset! tile-sprites-atom
              {floor (tile 0 0)
               wall  (tile 0 1)
               pit   (tile 1 0)})

      (resize-pixels! screen)

;      (position! screen (* 16 tile-size) (* 16 tile-size))
      ))
  
  :on-start-game
  (fn [screen _]
    (if @channel-atom (lamina/close @channel-atom))
    (reset! channel-atom (screen :channel)))

  :on-render
  (fn [screen _]
;    (println (:delta-time screen))
    (clear!)
    (if @channel-atom (handle-server-messages @channel-atom))
    (render! screen (organize-sprites)))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen))

  :on-add-guy
  (fn [screen entities]
    (println (str  "MOO!" (screen :guy)))
    (conj entities (screen :guy))))
