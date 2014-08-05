(ns misfits.screens.menu
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]))

(defn layout [screen]
  (let [ui-skin (skin "skin/uiskin.json")
        menu (table
              [[(text-button "Start Local Game" ui-skin) :width 256 :space-bottom 8]
               :row
               [(text-button "Connect To Server" ui-skin) :width 256 :space-bottom 8]
               :row
               (table [[(text-button "Options" ui-skin) :width 128 :space-left 24]
                       [(text-button "Quit" ui-skin) :width 128 :space-right 8]])]
              :align (align :center)
              :set-fill-parent true)]
    menu))

(defscreen menu-screen
  :on-show
  (fn [screen entities]
    (-> screen setup-camera! layout))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen)
    (layout screen))

  :on-touch-down
  (fn [event entities]
    (println (str "Touch event! " event ", " entities))))
