(ns misfits.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))

(def tile-size 32)

(def level ["HHHHHH HHHHHHH H"
            "H   HO   OH     "
            "H   O           "
            "H   H     H     "
            "HHOHH     H     "
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
                          [[x y] (case (-> level (nth (mod x 16)) (nth (mod y 16)))
                                   \O :pit
                                   \H :wall
                                   :floor)])))

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
          wall-front (texture (aget tiles-textures 0 1))
          wall-top (texture (aget tiles-textures 0 2))
          wall-back (texture (aget tiles-textures 0 3))
          pit (texture (aget tiles-textures 1 0))]

      (resize-pixels! screen)

      (position! screen (* 16 tile-size) (* 16 tile-size))

      (flatten
       (for [x (range 0 128) y (range 0 128)]
         (let [this-tile (floor-map [x y])
               up-tile (floor-map [x (inc y)])
               down-tile (floor-map [x (dec y)])
               
               results [(cond (= this-tile :floor) stone1
                               (= this-tile :wall) (if (= down-tile :wall) wall-top wall-front)
                               (= this-tile :pit) pit)]
               results (if (and (not= this-tile :wall) (= down-tile :wall))
                         (conj results wall-back)
                         results)]
           
           (map #(assoc % :x (* tile-size x) :y (* y tile-size)) results))))))
  
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
