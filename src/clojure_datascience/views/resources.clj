(ns clojure-datascience.views.resources)


(defn resource-view
  [{:keys [name url author alt-resources] :as resource}]
  [:div.resource
   [:h2
    [:a {:href url} name]]
   [:ul
    (for [attr [:author :alt-resources]]
      (when-let [v (resource attr)]
        [:li (str (name attr) ": " v)]))]])

(defn resources-view
  [resources]
  (->> resources
       (group-by :category)
       (map
         (fn [[cat cat-resources]]
           [:div.category
            [:h2 {:id (str "cat-" cat)} cat]
            ;(map resource-view cat-resources)]))
            ]))
       (into [:div#resources])))

