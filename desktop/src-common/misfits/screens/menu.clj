(ns misfits.screens.menu
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]))

(defn layout [screen]
  (let [font (bitmap-font "font/Averia-16.fnt")
        text-white (label "QUIT!!!" (style :label font (color :white)) :set-x 250 :set-y 150 )
        text-red (label "JOIN GAME!!!" (style :label font (color :red)) :set-x 200 :set-y 175)
        text-blue (label "NEW GAME!!!" (style :label font (color :blue)) :set-x 220 :set-y 200)
        cam (screen :camera)
        cam-width (.viewportWidth cam)
        cam-height (.viewportHeight cam)]
    [text-white text-red text-blue]))

(defscreen menu-screen
  :on-show
  (fn [screen entities]
    (-> screen setup-camera! layout))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (layout screen)))
