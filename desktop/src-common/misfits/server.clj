(ns misfits.server
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all])
  (:import [java.util UUID]))

(defn floor [rows]
  (reverse rows))

(defn trigger [x y] nil)
(defn open-door [x y] nil)

(def dungeon
  [(floor ["HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
           "H     H     H     HHHHHHHHHHHHHH"
           "H     H     H     HHHHHHHHHHHHHH"
           "H           O        HHHHHHH   H"
           "H     H     H     HH HHHHHHH HHH"
           "H     H O   H     HH HHHHHHH HHH"
           "HHH HHHHH HHHHHHHHHH         HHH"
           "HHH          HHHHHHH HH HHHHHHHH"
           "HHHHH HHHHHH HHHHHHH HH HHHHHHHH"
           "HHHHHOHHHHHH HHHHHHH HH HHHHHHHH"
           "HHHHH HHHHHH         HH HHHHHHHH"
           "HHH     HHHH HHHH HHHHH HHHHHHHH"
           "HHH     H    HHHH HHHHH  HHHHHHH"
           "HHHHHH HH HHHHHHH HHHHHH  HHHHHH"
           "H   HH HH HHHHHHH HHHHHHH HHHHHH"
           "H                 HHHHHHH HHHHHH"
           "H   HHHHHHHHHHHHHHHHHHHHH HHHHHH"
           "H   HHHHHHHHHH      HHHHH HHHHHH"
           "H   HHH   HHHH HHHH   HHH HHHHHH"
           "H   HHH        HHHHHH HHH HHHHHH"
           "H   HHH   HHHH   HHHH     HHHHHH"
           "H   HHHH HHHHHHH HHHHHHHHHHHHHHH"
           "H         HHHHHH HHHHHHHHHHHHHHH"
           "H         HHHHHH   HHHHHHHHHHHHH"
           "H         HHHHHHHH HHHHHHHHHHHHH"
           "HHH HHHH HHHHHHHHH HHHHHHHHHHHHH"
           "H     H   HHH   HH HHHHHHHHHHHHH"
           "H     H   HHH H    HHHHHHHHHHHHH"
           "H     H   HHH HHHHHHHHHHHHHHHHHH"
           "H     HHOHHHH HHHHHHHHHHHHHHHHHH"
           "H     HH          HHHHHHHHHHHHHH"
           "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"])

   (floor ["HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
           "H    HHHHH      H     HHHHHHHHHH"
           "H HH   O   HHHH H HHH HHHHHHHHHH"
           "H HHHHHHHHHH    H H   HHHHHHHHHH"
           "H    HHH     HHHH H H HHHHHHHHHH"
           "HHHH HHH HHH H    H H HHHHHHHHHH"
           "H      H H   H HHHH H HHHHHHHHHH"
           "H HH H H H HHH H    H HHHHHHHHHH"
           "H HH H   H     H HHHH HHHHHHHHHH"
           "H HH HHHHHHHHHHH HHHH HHHHHHHHHH"
           "H                H    HHHHHHHHHH"
           "H HHHHHHHHHH HHHHH HHHHHHHHHHHHH"
           "H H      HHH     H    HHHHHHHHHH"
           "H H H  H HHHHHHHHHH HHHHHHHHHHHH"
           "H H HHHH H     HHHH HHHHHHHHHHHH"
           "H   H      HHH HHHH      HHHHHHH"
           "HHHHHHHHHHHHHH HHHHHHHHH HHHHHHH"
           "HHHHHHHHHHHHHH HHHHHHHHH HHHHHHH"
           "HHHHHHHHHHHHH  HHHHHHHH  HHHHHHH"
           "HHHH   HHH    HHHHHHHH  HHHHHHHH"
           "HHHHHH     HHHHHHH     HHHHHHHHH"
           "HHHHHHHH HHHHHHHHH HHHHHHHHHHHHH"
           "HHHHHHHH HHHHHHHHH HHHHHHHHHHHHH"
           "HHHH     HHHHHH    HHHHHHHHHHHHH"
           "HHHH HHHHHHHHHH HHHHHHHHHHHHHHHH"
           "HHHH HHHH O HHH HHHHHHHHHHHHHHHH"
           "HHH   HHH   H   HHHHHHHHHHHHHHHH"
           "HH     HHHH   HHHHHHHHHHHHHHHHHH"
           "HH          HHHHHHHHHHHHHHHHHHHH"
           "HH     HHHHHHHHHHHHHHHHHHHHHHHHH"
           "HHH   HHHHHHHHHHHHHHHHHHHHHHHHHH"
           "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"])
   ])

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
    (notify client-id :floor {:floor (rand-nth dungeon)})))

(defn start-server
  "Starts server, returns a fn that takes no parameters and shuts down the server"
  []
  (aleph/start-tcp-server handle-new-client aleph-params))
