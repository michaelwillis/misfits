(ns misfits.screens.menu
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]))

(defn main-menu [screen]
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(text-button "Start Local Game" ui-skin :set-name "start-game") :width 256 :space-bottom 8]
      :row
      [(text-button "Connect To Server" ui-skin :set-name "connect") :width 256 :space-bottom 8]
      :row
      (table [[(text-button "Options" ui-skin :set-name "options") :width 128 :space-left 24]
              [(text-button "Quit" ui-skin :set-name "quit") :width 128 :space-right 8]])]
     :align (align :center)
     :set-fill-parent true)))

(defn difficulty-menu [screen]
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(label "Select Difficulty" ui-skin) :space-bottom 8]
      :row
      [(text-button "Hard" ui-skin :set-name "hard") :width 256 :space-bottom 8]
      :row
      [(text-button "Super Hard" ui-skin :set-name "super-hard") :width 256 :space-bottom 8]
      :row
      [(text-button "Super Duper Hard" ui-skin :set-name "super-duper") :width 256 :space-bottom 8]
      :row
      [(text-button "Back" ui-skin :set-name "main-menu") :width 128 :space-bottom 8]]
     :align (align :center)
     :set-fill-parent true)))

(defn connect-menu [screen]
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(label "Enter Server Address" ui-skin) :space-bottom 8]
      :row
      [(text-field "" ui-skin) :space-bottom 8]
      :row
      (table [[(text-button "Connect" ui-skin :set-name "connect") :width 128 :space-bottom 8]
              [(text-button "Back" ui-skin :set-name "main-menu") :width 128 :space-bottom 8]])]
     :align (align :center)
     :set-fill-parent true)))

(defn options-menu [screen]
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [(table [(check-box "Full Screen" ui-skin)
              (label "             " ui-skin)
              (select-box ui-skin :set-items (into-array ["1920x1080" "1440x900" "1280x768"]))])
      :row
      [(text-button "Back" ui-skin :set-name "main-menu") :width 64 :space-bottom 8]]
     :align (align :center)
     :set-fill-parent true)))

(defscreen menu-screen
  :on-show
  (fn [screen entities]
    (-> screen setup-camera! main-menu))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen)
    (options-menu screen))

  :on-ui-changed
  (fn [screen entities]
    (let [name (actor! (:actor screen) :get-name)]
      (println name)
      (case name
        "start-game" (difficulty-menu screen)
        "connect" (connect-menu screen)
        "main-menu" (main-menu screen)
        "options" (options-menu screen)
        entities))))
