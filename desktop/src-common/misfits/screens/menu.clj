(ns misfits.screens.menu
  (:require [lamina.core :as lamina]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]
            [misfits.screens.main :refer [main-screen]]
            [misfits.client :refer [connect]]
            [misfits.server :refer [start-server]])
  (:import [com.badlogic.gdx.scenes.scene2d.ui TextField$TextFieldListener]))

(def server-shutdown-fn (atom nil))
(def server-address (atom "127.0.0.1"))

(defn display-mode-to-vector [mode]
  [(.width mode) (.height mode)])

(defn display-mode-to-string [mode]
  (str (.width mode) "x" (.height mode)))

(defn set-resolution! [s]
  (let [mode (->> (graphics! :get-display-modes) (filter #(= s (display-mode-to-string %))) first)]
    (graphics! :set-display-mode (.width mode) (.height mode) (graphics! :is-fullscreen)))
  nil)

(defn set-fullscreen! [f]
  (graphics! :set-display-mode (graphics! :get-width) (graphics! :get-height) f)
  nil)

(defn main-menu []
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(image "title.png") :space-bottom 16]
      :row
      [(text-button "Start Local Game" ui-skin :set-name "start-game") :width 256 :space-bottom 8]
      :row
      [(text-button "Connect To Server" ui-skin :set-name "connect-menu") :width 256 :space-bottom 8]
      :row
      (table [[(text-button "Options" ui-skin :set-name "options") :width 128]
              [(text-button "Quit" ui-skin :set-name "quit") :width 128]])]
     :align (align :top)
     :set-fill-parent true)))

(defn difficulty-menu []
  (reset! server-address "127.0.0.1")
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(image "title.png") :space-bottom 16]
      :row
      [(label "Select Difficulty" ui-skin) :space-bottom 8]
      :row
      [(text-button "Hard" ui-skin :set-name "hard") :width 256 :space-bottom 8]
      :row
      [(text-button "Super Hard" ui-skin :set-name "super-hard") :width 256 :space-bottom 8]
      :row
      [(text-button "Super Duper Hard" ui-skin :set-name "super-duper") :width 256 :space-bottom 8]
      :row
      [(text-button "Back" ui-skin :set-name "main-menu") :width 128 :space-bottom 8]]
     :align (align :top)
     :set-fill-parent true)))

(defn connect-menu [error-message address]
  (let [ui-skin (skin "skin/uiskin.json")
        text-listener (proxy [TextField$TextFieldListener] []
                        (keyTyped [f c]
                          (reset! server-address (text-field! f :get-text))))]
    (table
     [[(image "title.png") :space-bottom 16]
      :row
      [(label "Enter Server Address" ui-skin) :space-bottom 8]
      :row
      [(text-field address ui-skin :set-text-field-listener text-listener)
       :width 256 :space-bottom 8]
      :row
      [(label error-message ui-skin :set-color (color :red)) :space-bottom 8]
      :row
      (table [[(text-button "Connect" ui-skin :set-name "connect") :width 128 :space-bottom 8]
              [(text-button "Back" ui-skin :set-name "main-menu") :width 128 :space-bottom 8]])]
     :align (align :top)
     :set-fill-parent true)))

(defn options-menu []
  (let [ui-skin (skin "skin/uiskin.json")]
    (table
     [[(image "title.png") :space-bottom 16]
      :row
      [(label "Options" ui-skin) :space-bottom 8]
      :row
      [(table [(check-box "Full Screen" ui-skin :set-name "fullscreen" :set-checked (graphics! :is-fullscreen))
               (label "             " ui-skin)
               (select-box ui-skin :set-name "resolution" :set-items (->> (graphics! :get-display-modes)
                                                                          (map (juxt display-mode-to-vector
                                                                                     display-mode-to-string))
                                                                          (into (sorted-map))
                                                                          (vals)
                                                                          (reverse)
                                                                          (into-array)))])
       :space-bottom 8]
      :row
      [(text-button "Back" ui-skin :set-name "main-menu") :width 64 :space-bottom 8]]
     :align (align :top)
     :set-fill-parent true)))

(defn start-game [difficulty]
  (if @server-shutdown-fn (lamina/wait-for-result (@server-shutdown-fn)))
  (reset! server-shutdown-fn (start-server))
  (run! main-screen :on-start-game :channel (connect "127.0.0.1"))
  [])

(defn connect-to-server [address]
  (try
    (let [ch (connect address)]
      (when @server-shutdown-fn 
        (lamina/wait-for-result (@server-shutdown-fn))
        (reset! server-shutdown-fn nil))

      (run! main-screen :on-start-game :channel (connect address))
      [])
    (catch Throwable t
      (connect-menu (str "Couldn't connect to " address) address))))

(defn toggle-menu [entities]
  (if (not (empty? @(main-screen :entities)))
    (if (empty? entities) (main-menu) [])))

(defscreen menu-screen
  :on-show
  (fn [screen entities]
    (setup-camera! screen)
    (main-menu))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen))

  :on-key-down
  (fn [screen entities]
    (let [k (:key screen)]
      (cond
       (= k (key-code :escape)) (toggle-menu entities)
       :else entities)))

  :on-ui-changed
  (fn [screen entities]
    (let [name (actor! (:actor screen) :get-name)]
      (case name
        "start-game" (difficulty-menu)
        "connect-menu" (connect-menu "" "")
        "options" (options-menu)
        "quit" (app! :exit)

        "main-menu" (main-menu)

        "hard" (start-game "hard")
        "super-hard" (start-game "super-hard")
        "super-duper" (start-game "super-duper")

        "connect" (connect-to-server @server-address)

        "fullscreen" (set-fullscreen! (check-box! (:actor screen) :is-checked))
        "resolution" (set-resolution! (select-box! (:actor screen) :get-selected))
        (do (println name) entities)))))
