(ns misfits.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [misfits.net.client :refer [handle-server-messages]]
            [misfits.screens.main :refer [main-screen]]
            [misfits.screens.menu :refer [menu-screen]]))

(def level ["HHHHHH_HHHHHHH_H"
            "H___HO___OH_____"
            "H___O___________"
            "H___H_____H_____"
            "HHOHH_____H_____"
            "H___HHHHHHHHH_HH"
            "H________H______"
            "_________H______"
            "H________H______"
            "HHHHHH_HHHHHHHHH"
            "H_______H_______"
            "H___H___H_______"
            "H___H___________"
            "H___HHHHHH_HH___"
            "H___H_______H___"
            "H___H_______H___"])

(defgame misfits
  :on-create
  (fn [this]
    (set-screen! this menu-screen)))


