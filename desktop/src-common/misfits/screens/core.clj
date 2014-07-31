(ns misfits.screens.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))

(defn pixel-size []
  (cond 
   (> 512 (game :height)) 1
   (> 1024 (game :height)) 2
   (> 1532 (game :height)) 3
   (> 2048 (game :height)) 4
   :else 5))

(defn resize-pixels! [screen]
  (println (str "Resize pixels! " screen))
  (height! screen (/ (game :height) (pixel-size))))

(defn setup-camera! [screen]
  (let [screen (update! screen :camera (orthographic) :renderer (stage))]
    (resize-pixels! screen)
    screen))
