(ns misfits.core.desktop-launcher
  (:require [misfits.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. misfits "misfits" 1280 720)
  (Keyboard/enableRepeatEvents true))
