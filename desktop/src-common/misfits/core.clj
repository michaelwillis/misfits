(ns misfits.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.screens.main :refer [main-screen]]
            [misfits.screens.menu :refer [menu-screen]]))

(defgame misfits
  :on-create
  (fn [this]
    (set-screen-wrapper! (fn [screen-atom screen-fn]
                           (try (screen-fn)
                                (catch Exception e
                                  (.printStackTrace e)))))
    (set-screen! this main-screen menu-screen)))
