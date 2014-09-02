(ns misfits.net
  (:require [clojure.edn :as edn]
            [lamina.core :as lamina]
            [gloss.core :as gloss]))

(def aleph-params {:port 13884 :frame (gloss/string :utf-8 :delimiters ["\n"])})

(defn ednify-channel [raw-channel]
  (let [emitter (lamina/map* edn/read-string raw-channel)
        receiver (lamina/channel)]
    (lamina/siphon (lamina/map* str receiver) raw-channel)
    (lamina/splice emitter receiver)))
