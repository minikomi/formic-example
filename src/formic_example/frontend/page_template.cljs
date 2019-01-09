(ns formic-example.frontend.page-template
  (:require [reagent.core :as r]
            [reagent.impl.component :refer [extract-props]]))

(defn title-wide [{:keys [page-data]}]
  [:div.title-wide

   {:style (if (:hero-image page-data)
             {:background-position 'center
              :background-color "#777"
              :background-repeat 'no-repeat
              :background-size 'cover
              :background-image (str "url(" (:hero-image page-data) "&w=600&h=400)")}
             {:background-color "#777"})
    :class [:w-100 :pv5 :tc]}
   [:div.txt
    {:style {:color (:title-color page-data)}}
    [:h1
     {:class [:w-100 :tc :w2]}
     (:title-text page-data)]
    [:h2
     (:date-created page-data)]
    (when (:subtitle-text page-data)
      [:h3 (:subtitle-text page-data)])]])

(defn title-normal [{:keys [page-data]}]
  [:div.title-normal
   (if (:hero-image page-data)
     [:img {:class [:w-40 :dib]
            :src (str (:hero-image page-data) "&w=600&h=400&fit=clamp")}]
     [:span.placeholder
      {:class [:bg-light-gray
               :dib
               :w-40
               :h5]}])
   [:div.title-txt
    {:style {:color (:title-color page-data)}
     :class [:dib :w-60 :v-top :pl4]}
    [:h1
     {:class []}
     (:title-text page-data)]
    [:h2
     (:date-created page-data)]
    (when (:subtitle-text page-data)
      [:h3 (:subtitle-text page-data)])]])

(defn gallery [field]
  [:pre (prn-str field)])

(defn paragraph [field render-quill]
  (let [el (r/atom nil)]
    (r/create-class
     {:reagent-render
      (fn [field render-quill]
        (.setContents render-quill
                      (clj->js (:body field)))
        [:div.paragraph
         (when (:title field)
           [:h3.subheader
            {:class [:f4]}
            (:title field)])
         [:div.paragraph-contents
          {:class [:pv2 :f5 :w-90 :ma0]
           :dangerouslySetInnerHTML
           {:__html (.. render-quill -root -innerHTML)}}]])})))

(defn page [data]
  (let [render-quill
        (let [el (js/document.createElement "div")]
          (js/Quill. el))]
    (fn [data]
      [:div.page
       {:style {:margin-left "35%" :width "60%"}}
       (case (get-in data [:page-data :title-type])
         "wide" [title-wide data]
         [title-normal data])
       (doall
        (for [n (range (count (:article-body data)))
              :let [field (nth (:article-body data) n)]]
          ^{:key n}
          [:div.body-field
           (case (:compound field)
             :gallery [gallery field]
             :paragraph [paragraph field render-quill])]))
       [:pre (with-out-str (cljs.pprint/pprint data))]])))
