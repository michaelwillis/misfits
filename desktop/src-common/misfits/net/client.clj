(ns misfits.net.client
  (:require [aleph.tcp :as aleph]
            [lamina.core :as lamina]
            [misfits.net.core :refer :all]))

(defn connect-client [server-host]
  (-> aleph-params 
      (assoc :host server-host)
      aleph/tcp-client
      lamina/wait-for-result
      ednify-channel))
