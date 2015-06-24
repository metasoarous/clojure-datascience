(ns clojure-datascience.core
  (:require [clojure-datascience.handler :refer [make-handler]]
            [clojure-datascience.config :refer [new-config]]
            [clojure-datascience.resources :refer [new-db]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [log info]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))


(defrecord WebServer [config database jetty]
  component/Lifecycle
  (start [component]
    (info "Starting Jetty server")
    (assoc component :jetty
           (run-jetty (make-handler database) {:port (:server-port config) :join? false})))

  (stop [component]
    (info "Stopping Jetty server")
    (.stop (:jetty component))
    (assoc component :jetty nil)))

(defn new-web-server []
  (map->WebServer {}))

(defn new-system
  ([overrides]
   (component/system-map
     :config   (new-config overrides)
     :database (component/using (new-db) [:config])
     :web-server (component/using (new-web-server) [:config :database])))
  ([] (new-system {})))

(defonce system nil)

(defn init-if-needed
  []
  (when-not system
    (alter-var-root #'system new-system)))

(defn reinit
  []
  (alter-var-root #'system (constantly (new-system {}))))

(:database system)
(defn start
  ([options]
   (init-if-needed)
   (alter-var-root #'system #(assoc % :overrides options))
   (start))
  ([]
   (init-if-needed)
   (alter-var-root #'system component/start)))

(defn stop
  []
  (alter-var-root #'system (fn [s] (when s (component/stop s)))))

(defn restart
  ([] (stop) (start))
  ([options] (stop) (start options)))

(defn -main [& [port]]
  (let [opts (if port
               {:server-port (Integer/parseInt port)}
               {})]
    (component/start (new-system opts))))


