(ns clojure-datascience.routes.home
  (:require [clojure-datascience.layout :as layout]
            [clojure-datascience.views.resources :as rcs]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :as hc]
            [clojure.java.io :as io]))


(defn home-page []
  (layout/render
    "home.html"
    {:resources (-> (io/resource "data/resources.edn")
                    slurp
                    read-string
                    rcs/resources-view)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))


