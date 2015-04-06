(ns clojure-datascience.views.resources)


(defn resource-view
  [{:keys [url author alt-resources] :as resource}]
  [:div.resource
   [:h3
    [:a {:href url} (:name resource)]]
   [:ul
    (for [attr [:author :alt-resources]]
      (when-let [v (get resource attr)]
        [:li (str (name attr) ": " v)]))]])

(defn resources-view
  [resources]
  (->> resources
       (group-by :category)
       (map
         (fn [[cat cat-resources]]
           (into
             [:div.category
              [:h2 {:id (str "cat-" cat)} cat]]
             (map resource-view cat-resources))))
       (into [:div#resources])))

