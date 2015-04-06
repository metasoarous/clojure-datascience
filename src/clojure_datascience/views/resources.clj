(ns clojure-datascience.views.resources)


(defn alt-resource-view
  [{:keys [title url]}]
  [:a {:href url} title])

(defmulti attr-view
  (fn [attr & _]
    (if (#{:alt-resources :tags} attr)
      attr
      ::standard-attr-view)))

(defmethod attr-view ::standard-attr-view
  [attr data]
  data)

(defmethod attr-view :alt-resources
  [attr alt-resources]
  (map alt-resource-view alt-resources))

(defmethod attr-view :tags
  [attr tags]
  (clojure.string/join ", " tags))


(defn resource-view
 [{:keys [url author alt-resources] :as resource}]
  [:div.resource
   [:h4
    [:a {:href url} (:name resource)]]
   [:ul
    (for [attr [:author :alt-resources :tags :description]]
      (when-let [v (get resource attr)]
        (into [:li (str (name attr) ": ")]
              (attr-view attr v))))]])

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

