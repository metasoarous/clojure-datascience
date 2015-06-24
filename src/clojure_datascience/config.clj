(ns clojure-datascience.config
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre :refer [log info]]))

(def config-defaults
  {:server-port 3000})


(def config-parsers
  [[:server-port #(Integer/parseInt %)]
   [:mongo-uri   identity]])


(defrecord Config [overrides]
  component/Lifecycle
  (start [component]
    (info "Reading env variables for configuration")
    (reduce
      (fn [c [k parser]]
        (assoc c k (if-let [v (env k)]
                     (parser v)
                     (get (merge config-defaults overrides) k))))
      component
      config-parsers))

  (stop [component]
    (info "Closing config (no-op)")
    component))


(defn new-config
  [overrides]
  (Config. overrides))


