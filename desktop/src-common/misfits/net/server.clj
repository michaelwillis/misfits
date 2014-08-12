(ns misfits.net.server
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all])
  (:import [java.util UUID]))

(def level1 ["HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "H_____H_____H_____HHHHHHHHHHHHHH"
             "H_____H_____H_____HHHHHHHHHHHHHH"
             "H___________O________HHHHHHH____"
             "H_____H_____H_____HH_HHHHHHH_HHH"
             "H_____H_O___H_____HH_HHHHHHH_HHH"
             "HHH_HHHHH_HHHHHHHHHH_________HHH"
             "HHH__________HHHHHHH_HHHHHHHHHHH"
             "HHHHH_HHHHHH_HHHHHHH_HHHHHHHHHHH"
             "HHHHHOHHHHHH_HHHHHHH_HHHHHHHHHHH"
             "HHHHH_HHHHHH_________HHHHHHHHHHH"
             "HHH_____HHHH_HHHH_HHHHHHHHHHHHHH"
             "HHH_____H____HHHH_HHHHHHHHHHHHHH"
             "HHHHHH_HH_HHHHHHH_HHHHHHHHHHHHHH"
             "HHHHHH_HH_HHHHHHH_HHHHHHHHHHHHHH"
             "HHHH______________HHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"])

(def level2 ["HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "H____H_O_H______H_____HHHHHHHHHH"
             "H_HH_______HHHH_H_HHH_HHHHHHHHHH"
             "H_HHHHHHHHHH____H_H___HHHHHHHHHH"
             "H____HHH_____HHHH_H_H_HHHHHHHHHH"
             "HHHH_HHH_HHH_H____H_H_HHHHHHHHHH"
             "H______H_H___H_HHHH_H_HHHHHHHHHH"
             "H_HH_H_H_H_HHH_H____H_HHHHHHHHHH"
             "H_HH_H___H_____H_HHHH_HHHHHHHHHH"
             "H_HH_HHHHHHHHHHH_HHHH_HHHHHHHHHH"
             "H________________H____HHHHHHHHHH"
             "H_HHHHHHHHHH_HHHHH_HHHHHHHHHHHHH"
             "H_H______HHH_____H____HHHHHHHHHH"
             "H_H_H__H_HHHHHHHHHHHHHHHHHHHHHHH"
             "H_H_HHHH_H_____HHHHHHHHHHHHHHHHH"
             "H___H______HHH_HHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHH_HHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
             "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"])

(def client-channels (atom {}))

(defn notify [client-id topic message]
  (lamina/enqueue (@client-channels client-id)
                  [topic message]))

(defn notify-all [topic message]
  (doseq [client-id (keys @client-channels)]
    (notify client-id topic message)))

(defn handle-new-client [raw-channel client]
  (let [client-id (UUID/randomUUID)]
    (swap! client-channels #(assoc % client-id (ednify-channel raw-channel)))
    (lamina/on-closed raw-channel (fn [] (swap! client-channels #(dissoc % client-id))))
    (notify client-id :floor {:floor (rand-nth [level1 level2])})))

(defn start-server
  "Starts server, returns a fn that takes no parameters and shuts down the server"
  []
  (aleph/start-tcp-server handle-new-client aleph-params))
