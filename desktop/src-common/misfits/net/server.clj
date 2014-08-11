(ns misfits.net.server
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all])
  (:import [java.util UUID]))

(def level ["HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
            "H_____H_____H_____HHHHHHHHHHHHHH"
            "H_____H_____H_____HHHHHHHHHHHHHH"
            "H___________O________HHHHHHH____"
            "H_____H_____H_____HH_HHHHHHH_HHH"
            "H_____H_____H_____HH_HHHHHHH_HHH"
            "HHH_HHHHH_HHHHHHHHHH_________HHH"
            "HHH_HHHHH_HHHHHHHHHH_HHHHHHHHHHH"
            "HHH__________HHHHHHH_HHHHHHHHHHH"
            "HHHHHHHHHHHH_HHHHHHH_HHHHHHHHHHH"
            "HHHHHHHHHHHH_________HHHHHHHHHHH"
            "HHH____HHHHH_HHHH_HHHHHHHHHHHHHH"
            "HHH____HH____HHHH_HHHHHHHHHHHHHH"
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
    (lamina/on-closed raw-channel (fn [] (swap! client-channels #(dissoc % client-id))))))

(defn start-server
  "Starts server, returns a fn that takes no parameters and shuts down the server"
  []
  (aleph/start-tcp-server handle-new-client aleph-params))
