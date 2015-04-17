(ns clojure-datascience.core
  (:require [clojure-datascience.handler :refer [app]]
            [clojure-datascience.config :refer [new-conifg]]
            [clojure-datascience.resources :refer [new-db]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [log info]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))


(defrecord JettyServer [config]
  component/Lifecycle
  (start [component]
    (info "Starting Jetty server")
    (assoc component
           :jetty-server
           (run-jetty app {:port (:server-port config) :join? false})))

  (stop [component]
    (info "Stopping Jetty server")
    (.stop (:jetty-server component))
    (assoc component :jetty-server nil)))

(defn new-jetty-server []
  (map->JettyServer. {}))

(defn system
  [overrides]
  (component/system-map
    :config   (new-config overrides)
    :database (component/using (new-db) [:config])
    :jetty-server (component/using (new-jetty-server) [:config :database])))

(defn -main [& [port]]
  (let [opts (if port
               {:server-port (Integer/parseInt port)}
               {})]
    (component/start (system opts))))


