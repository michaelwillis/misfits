(ns misfits.screens.title
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]))

(defn layout [screen]
  (resize-pixels! screen)

  (let [title-texture (texture "title.png")
        title-width (.getRegionWidth (:object title-texture))
        title-height (.getRegionHeight (:object title-texture))

        cam (screen :camera)
        cam-width (.viewportWidth cam)
        cam-height (.viewportHeight cam)

        entities [(assoc title-texture 
                    :y (- cam-height title-height)
                    :x (- (/ cam-width 2) (/ title-width 2)))]]

    entities))

(defscreen title-screen
  :on-show
  (fn [screen entities]
    (-> screen setup-camera! layout))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (layout screen)))
