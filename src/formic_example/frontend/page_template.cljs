(ns formic-example.frontend.page-template
  (:require [reagent.core :as r]
            [cljsjs.react-flip-move]
            [delta-to-hiccup.core :refer [to-hiccup]]
            [reagent.impl.component :refer [extract-props]]))

(defn title-wide [{:keys [page-data]}]
  [:div.title-wide
   {:style (if (:hero-image page-data)
             {:text-shadow "1px 2px 3px #000"
              :background-position 'center
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

(defn article-title [data]
  (case (get-in data [:page-data :title-type])
    "wide" [title-wide data]
    [title-normal data]))

(defn gallery [field]
  (let [current-image (r/atom 0)]
    (r/create-class
     {:component-will-update
      (fn [_ [_ new-props]]
        (let [images (:images new-props)]
          (when (<= (count images) @current-image)
            (reset! current-image 0))))
      :reagent-render
      (fn [field]
        (when (some :image (:images field))
          [:div.gallery
           (let [i (nth (:images field) @current-image)]
             [:div.current
              {:class [:w-100]}
              [:img {:src (str (:image i) "&w=600&h=400&fit=clamp")
                     :class [:db :w-100]}]
              [:p {:class [:dib :w-100]} (:caption i)]])
           [:ul
            {:class [:list :pa0 :mt2 :mh0 :relative]
             :style {:z-index 1}}
            (doall
             (for [n (range (count (:images field)))
                   :let [i (nth (:images field) n)]]
               (when (:image i)
                 ^{:key n}
                 [:li
                  {:class [:dib :w-20 :pa0]}
                  [:a
                   {:href "#"
                    :class (if (= n @current-image)
                             [:ba :b--pink :pa2 :db]
                             [:grow :ba :b--white :pa2 :db]
                             )
                    :on-click (fn [ev]
                                (.preventDefault ev)
                                (reset! current-image n))}
                   [:img {:class [:w-100]
                          :src
                          (str (:image i) "&w=100&h=100&fit=clamp")
                          }]]])))]]))})))

(defn paragraph [field]
  (let [el (r/atom nil)]
    (r/create-class
     {:reagent-render
      (fn [field]
        [:div.paragraph
         (when (:title field)
           [:h3.subheader
            {:class [:f4]}
            (:title field)])
         [:div.paragraph-contents
          {:class [:pv2 :f5 :w-90 :ma0]}
            (to-hiccup (js->clj (:body field) :keywordize-keys true))]])})))

(def flip-move (r/adapt-react-class js/FlipMove))

(defn article-body [data]
  [flip-move
   {:duration 220}
   (doall
    (for [n (range (count (:article-body data)))
          :let [field (nth (:article-body data) n)]]
      ^{:key (:id (meta field))}
      [:div.body-field
       {:class [:mt4]}
       (case (:compound field)
         :gallery [gallery field]
         :paragraph [paragraph field])]))])

(defn page [data]
  (fn [data]
    [:div.page
     {:style {:margin-left "35%" :width "60%"
              :z-index 0
              :position :relative}}
     [article-title data]
     [article-body data]
     [:pre (with-out-str (cljs.pprint/pprint data))]]))
