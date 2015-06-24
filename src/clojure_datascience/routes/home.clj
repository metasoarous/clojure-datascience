(ns clojure-datascience.routes.home
  (:require [clojure-datascience.layout :as layout]
            [clojure-datascience.views.resources :as rcs]
            [compojure.core :as compojure :refer [defroutes GET POST]]
            [com.stuartsierra.component :as component]
            [hiccup.core :as hc]
            [noir.response :as res]
            [clojure.java.io :as io]))

;(defrecord HomeRoutes [database routes]
  ;component/Lifecycle
  ;(start [component]
    ;(assoc component
           ;:routes (compojure/routes
                     ;(GET "/" [] (home-page database))

(defn home-page [req]
  (layout/render
    "home.html"
    {:resources (-> (io/resource "data/resources.edn")
                    slurp
                    read-string
                    rcs/resources-view)}))

(defn about-page []
  (layout/render
    "about.html"
    {:about (-> "md/about.md" io/resource slurp)}))


(defn handle-submission
  [{:keys [params database]}]
  (res/redirect "/"))


(defroutes home-routes
  (GET "/" [req] (home-page req))
  (GET "/about" [] (about-page))
  (POST "/submit" [req] (handle-submission req)))

