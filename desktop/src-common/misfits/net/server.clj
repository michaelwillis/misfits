(ns misfits.net.server
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all])
  (:import [java.util UUID]))

(def server (atom nil))
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

(defn start-server []
  (if @server (throw (Exception. "Server already started!")))
  (reset! server (aleph/start-tcp-server handle-new-client aleph-params)))
