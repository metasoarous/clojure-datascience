(ns clojure-datascience.config
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre :refer [log info]]))

(def config-defaults
  {:server-port 3000})


(def config-parsers
  [[:server-port #(Integer/parseInt)]
   [:mongo-uri   identity]])


(defrecord Config [overrides]
  component/Lifecycle
  (start [component]
    (info "Reading env variables for configuration")
    (let [config
          (reduce
            (fn [c [k parser]]
              (if-let [v (env k)]
                (assoc k (parser v))))
            config-defaults
            config-parsers)]
      (assoc component
             :config
             (merge config overrides))))

  (stop [component]
    (info "Closing config")
    (assoc component :config nil)))


(defn new-config
  [[overrides]]
  (Config. overrides))


