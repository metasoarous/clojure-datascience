(ns clojure-datascience.resources
  (:require [monger.core :as mg]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [log info]]))


(defrecord MongoDatabase [config]
  component/Lifecycle
  (start [component]
    (info "Opening mongo connection")
    (let [conn (if (:mongo-uri config)
                 (mg/connect-via-uri (:mongo-uri env))
                 (mg/connect))]
      (assoc component :mongo-connection conn)))

  (stop [component]
    (info "Closing mongo connection")
    (mg/disconnect (:mongo-connection component))
    (assoc component :mongo-connection nil)))


(defn new-db []
  (map->MongoDatabase {}))

