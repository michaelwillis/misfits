(ns misfits.sprites
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [misfits.tiles :refer :all]))

(defn load-tile-sprites []
  (let [sheet (texture "LPC_Tile_Atlas/tiles.png")
        sprites (texture! sheet :split 64 64)
        tile (fn [x y] (texture (aget sprites x y)))]
    {floor (tile 0 0)
     wall  (tile 0 1)
     pit   (tile 1 0)}))
