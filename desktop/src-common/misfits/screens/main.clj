(ns misfits.screens.main
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.net.client :refer [connect handle-server-messages]]))

(defn resize-pixels! [screen]
  (let [pixel-size (if (< 768 (game :height)) 3 2)]
    (height! screen (/ (game :height) pixel-size))))

(def tile-size 32)

;(def channel (atom nil))
(def sprites (atom nil))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [camera (orthographic)
          screen (update! screen :camera camera :renderer (stage))
          tiles-sheet (texture "LPC_Tile_Atlas/tiles.png")
          tiles-textures (texture! tiles-sheet :split tile-size tile-size)]

;      (reset! channel (connect "127.0.0.1"))

      (reset! sprites {:stone1 (texture (aget tiles-textures 0 0))
                       :wall-front (texture (aget tiles-textures 0 1))
                       :wall-top (texture (aget tiles-textures 0 2))
                       :wall-back (texture (aget tiles-textures 0 3))
                       :pit  (texture (aget tiles-textures 1 0))})

      (resize-pixels! screen)

      (position! screen (* 16 tile-size) (* 16 tile-size))))
  
  :on-start-game
  (fn [screen entities]
    (println (keys screen)))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
;         (handle-server-messages @channel @sprites)
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen)))
