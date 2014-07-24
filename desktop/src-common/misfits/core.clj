(ns misfits.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))

(def tile-size 32)

(def level ["HHHHHH HHHHHHH H"
            "H   H     H     "
            "H               "
            "H   H     H     "
            "HH HH     H     "
            "H   HHHHHHHHH HH"
            "H        H      "
            "         H      "
            "H        H      "
            "HHHHHH HHHHHHHHH"
            "H       H       "
            "H   H   H       "
            "H   H           "
            "H   HHHHHH HH   "
            "H   H       H   "
            "H   H       H   "])

(def floor-map (into {} (for [x (range 0 64) y (range 0 64)]
                          [[x y] (if (= \space (-> level (nth (mod x 16))
                                                   (nth (mod y 16))))
                                   :floor :wall)])))

(defn resize-pixels! [screen]
  (let [pixel-size (if (< 768 (game :height)) 3 2)]
    (height! screen (/ (game :height) pixel-size))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [camera (orthographic)
          screen (update! screen :camera camera :renderer (stage))
          tiles-sheet (texture "LPC_Tile_Atlas/tiles.png")
          tiles-textures (texture! tiles-sheet :split tile-size tile-size)
          stone1 (texture (aget tiles-textures 0 0))
          stone2 (texture (aget tiles-textures 0 0))
          stone3 (texture (aget tiles-textures 0 0))
          wall-bottom (texture (aget tiles-textures 0 1))
          wall-corner (texture (aget tiles-textures 0 2))
          wall-top (texture (aget tiles-textures 0 3))
          ]

      (resize-pixels! screen)

      (position! screen (* 16 tile-size) (* 16 tile-size))

      (flatten
      (for [x (range 0 128) y (range 0 128)]
        (let [this-tile (floor-map [x y])
              up-tile (floor-map [x (inc y)])
              down-tile (floor-map [x (dec y)])
              tile (cond (and (= this-tile :wall) (= down-tile :floor)) wall-corner
                              (= this-tile :wall) wall-top
                              (= up-tile :wall) wall-bottom
                         :default (rand-nth [stone1 stone1 stone1 stone1
                                             stone1 stone2 stone2 stone3]))]
          [(assoc tile :x (* tile-size x) :y (* y tile-size))]))
      )))

  :on-render
  (fn [screen entities]
    (clear!)

    (orthographic! screen :translate 1 1)
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen)))

(defgame misfits
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
