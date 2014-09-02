(ns misfits.client
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all]
            [play-clj.core :refer :all]))

(defn connect [server-host]
  (-> aleph-params 
      (assoc :host server-host)
      aleph/tcp-client
      lamina/wait-for-result
      ednify-channel))

(defn notify [channel topic message]
  (lamina/enqueue channel [topic message]))
