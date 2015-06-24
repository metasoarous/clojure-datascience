(ns clojure-datascience.handler
  (:require [compojure.core :refer [defroutes routes]]
            [clojure-datascience.routes.home :refer [home-routes]]
            [clojure-datascience.middleware
             :refer [development-middleware production-middleware]]
            [clojure-datascience.session :as session]
            [com.stuartsierra.component :as component]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "clojure-datascience.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "\n-=[ clojure-datascience started successfully"
               (when (env :dev) "using the development profile") "]=-"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "clojure-datascience is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))


;; Compoent compatibility stuff (al. a Stuart Sierra's approach of injecting rout handler dependencies into
;; the request hash...); here we're modifying a bit to make it clear that it's the database that we're
;; associng in. Also note that we're using a more custom key name for this than ::web-app

(defn wrap-app-component [f database]
  (fn [req]
    (f (assoc req :clojure-datascience.component/database database))))

(defn make-handler [database]
  (-> (routes
        home-routes
        base-routes)
      (wrap-app-component database)
      development-middleware
      production-middleware))

