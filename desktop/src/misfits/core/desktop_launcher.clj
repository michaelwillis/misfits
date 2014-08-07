(ns misfits.core.desktop-launcher
  (:require [misfits.core :refer :all])
  (:import [java.awt Toolkit]
           [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (let [config (LwjglApplicationConfiguration. )]
    (set! (. config width) 800)
    (set! (. config height) 600)
    (set! (. config title) "Misfits")
    (set! (. config resizable) false)
    (LwjglApplication. misfits config)
    (Keyboard/enableRepeatEvents true)))
