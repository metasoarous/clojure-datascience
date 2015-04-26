(ns clojure-datascience.resources
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [log info]]))


(defrecord MongoDatabase [config]
  component/Lifecycle
  (start [component]
    (info "Opening mongo connection")
    (let [conn (if (:mongo-uri config)
                 (mg/connect-via-uri (:mongo-uri env))
                 (mg/connect))
          mongo-db (mg/get-db conn "clojure-datascience")]
      (assoc component
             :mongo-connection conn
             :mongo-db mongo-db)))

  (stop [component]
    (info "Closing mongo connection")
    (mg/disconnect (:mongo-connection component))
    (assoc component
           :mongo-connection nil
           :mongo-db nil)))


(defn new-db []
  (map->MongoDatabase {}))


(def attrs [:name :category :author :url :alt-resources :tags :description :posted-by-name :posted-by-email])

(defn filter-attrs
  [data]
  (let [attrs-set (set attrs)]
    (->> data
         (filter (fn [[k v]] (attrs-set k)))
         (into {}))))

(defn post-attrs
  [database data]
  (mc/insert (:mongo-db database) "resources" (filter-attrs data)))



