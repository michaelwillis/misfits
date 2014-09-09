(ns misfits.screens.main
  (:require [lamina.core :as lamina]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.core :refer :all]
            [misfits.sprites :refer :all]))

(def tile-size 32)  ; in pixels

(declare main-screen)

(defn tile-sprite [y x tile]
  (if-let [sprite (-> main-screen :screen deref :tile-sprites (get tile))]
    (assoc sprite :layer :tile :x (-> x (* tile-size) (- 32)) :y (-> y (* tile-size) (- 32)))))

(defn organize-sprites [screen]
  (->> (screen :tiles)
       (map-indexed #(map-indexed (partial tile-sprite %1) %2))
       (apply concat)))

(defn handle-server-messages [screen]
  (let [ch (screen :channel)]
    (when (and ch (< 0 (count ch)))
      (let [[topic message] @(lamina/read-channel ch)]
        (case topic
          :tiles (update! screen :tiles (message :tiles))
          (println (str "got unknown message! " topic ", " message))))
      (recur screen))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (println (keys screen))
    (-> screen
        (update! :camera (orthographic))
        (update! :renderer (stage))
        (update! :tile-sprites (load-tile-sprites))
        (resize-pixels!)
        ; (position! (* 16 tile-size) (* 16 tile-size))
    ))
  
  :on-start-game
  (fn [screen _]
    (if (screen :channel) (lamina/close (screen :channel)))
    (update! screen :channel (screen :channel)))

  :on-render
  (fn [screen _]
    (clear!)
    (if (screen :channel) (handle-server-messages screen))
    (render-sorted! screen (organize-sprites screen)))

  :on-resize
  (fn [screen entities]
    (resize-pixels! screen))

  :on-add-guy
  (fn [screen entities]
    (update! screen :guy (screen :guy))
    (println (str  "MOO!" (keys screen)))))
