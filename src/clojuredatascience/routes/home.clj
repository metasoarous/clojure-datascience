(ns clojure-datascience.routes.home
  (:require [clojuredatascience.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :as hc]
            [clojure.java.io :as io]))


(defn hcify-resource
  [{:keys [name url author alt-resources] :as resource}]
  [:div.resource
   [:h2
    [:a {:href url} name]]
   [:ul
    (for [attr [:author :alt-resources]]
      (when-let [v (resource attr)]
        [:li (str (name attr) ": " v)]))]])

(defn hcify-resources
  [resources]
  (println (group-by :category resources))
  (->> resources
       (group-by :category)
       (map
         (fn [[cat cat-resources]]
           [:div.category
            [:h2 {:id (str "cat-" cat)} cat]
            ;(map hcify-resource cat-resources)]))
            ]))
       (into [:div#resources])))

(defn home-page []
  (layout/render
    "home.html"
    {:resources (-> (io/resource "data/resources.edn")
                    slurp
                    read-string
                    hcify-resources)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))


