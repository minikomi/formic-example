(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [struct.core :as st]
            [formic.validation :as fv]
            [formic.components.date-picker :as dp]
            [cljs-time.core :as t]
            [cljs-time.coerce :refer [to-long]]
;            [formic.components.quill :as quill]
            [cljs.pprint :refer [pprint]]
            [formic.field :as formic-field]
            [formic.util :as u]
            [reagent.core :as r]
            [goog.dom :as gdom]
            [formic.frontend :as ff]))

(defn map->lat-lng [{:keys [lat lng]}]
  (when (and lat lng)
   (js/google.maps.LatLng. lat lng)))

(defn google-map [{:keys [current-value
                          err
                          ] :as f}]
  (let [state (r/atom :init)
        map (r/atom nil)]
   (r/create-class

    {:component-will-mount
     (fn [_ _]
       (when (and js/navigator.geolocation
                  (and
                   (nil? (:lat @current-value))
                   (nil? (:lng @current-value))))
         (js/navigator.geolocation.getCurrentPosition
          (fn [pos]
            (swap! current-value assoc
                   :lat (.. pos -coords -latitude)
                   :lng (.. pos -coords -longitude)))))
       (r/track! (fn []
                   (when (and @map
                              (:lat @current-value)
                              (:lng @current-value))
                     (.setCenter @map
                                 (clj->js
                                  {:lat (:lat @current-value)
                                   :lng (:lng @current-value)}))))))
     :component-did-mount
     (fn [this _]
       (reset! map
        (js/google.maps.Map.
         (gdom/getElementByClass "map-holder" (r/dom-node this))
         (clj->js {:center (js/google.maps.LatLng. 35.6895 139.6917)
                   :zoom 12}))))
     :reagent-render
     (fn [{:keys [current-value err] :as f}]
      [:div
       [:span.formic-input-title (u/format-kw (:id f))]
       [:div.formic-google-map
        [:div.map-wrapper
         [:div.map-holder]]]])})))


(def url-regex #"https?://(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")

(def validate-url
  {:message "有効なURLを入力してください"
   :validate (fn [txt]
               (or
                (empty? txt)
                (re-matches url-regex txt)))})

(defn date-active? [d]
  (not
   (t/equal? d (t/today))))

(def validate-date
  {:message "今日以外の日を選んで下さい"
   :optional true
   :validate date-active?})

(def page-details-field
  {:fields
   [{:id :title-text
     :type :string
     :validation [st/required]}
    {:id :place
     :type :lat-lng
     :validation []}
    {:id :page-type
     :type :radios
     :options {"photo" "Photo"}}
    {:id :date-created
     :default (t/today)
     :type :date
     :active? date-active?
     :validation [st/required validate-date]}
    {:id :title-type
     :type :radios
     :options {"wide" "Wide"
               "normal" "Normal"}
     :validation [st/required]}
    {:id :subtitle-text
     :type :string
     :validation []}
    {:id :issue-text
     :type :string
     :validation []}]})

(def name-field
  {:fields
   [{:id :name
     :type :string
     :validation [st/required]}
    {:id :url
     :type :string
     :validation [validate-url]}]})

(def credit-field
  {:fields
   [{:id :role
     :type :select
     :options {"hair-makeup" "HAIR & MAKE-UP"
               "hair" "HAIR"
               "photo" "PHOTO"
               "model" "MODEL"
               "clothes" "CLOTHES"
               "styling" "STYLING"}
     :validation [st/required]}
    {:id :names
     :flex [:name]}]})

(def test-images ["https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_1007.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_1023.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_10263.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_10715.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_10822.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_10832.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_10982.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_11006.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_11172.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_11182.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_1126.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_1128.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_11432.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_1145.jpg",
                  "https://images.dog.ceo/breeds/hound-afghan/n02088094_115.jpg"])

(def photo-credit-field
  {:fields
   [{:id :images
     :type :checkboxes
     :options
     (map-indexed
      (fn [n src]
        [n
         [:img
          {:src src}]])
      test-images)}
    {:id :photo-text
     :type :text
     :validation [st/required]}]})

(def compound-fields
  {:page page-details-field
   :name name-field
   :credit credit-field
   :photo-credit photo-credit-field})

(def form-fields
  [{:id :page-data
    :compound :page}
   {:id :credits
    :flex [:credit]}
   {:id :photo-credits
    :flex [:photo-credit]}])

(def form-schema
  {:id :test-form
   ;; compound fields
   :compound compound-fields
   ;; form fields
   :fields form-fields
   ;; serializers
   :serializers {
                 :date dp/DEFAULT_SERIALIZER
                 }
   :parsers {
             :date dp/DEFAULT_PARSER
             }
   :components {
                :date dp/date-picker
                :lat-lng google-map
                }
   :values {:page-data
            {:page-type "photo"
             :title-text "title text value"
             :title-type "normal"
             :date-created "2014-06-12"
             :subtitle-text "subtitle value"
             :issue-text "issue text"}
            :credits
            [{:_compound :credit
              :role "hair"
              :names
              [{:_compound :name :name "john" :url "http://google.com"}
               {:_compound :name :name "kaneko" :url "http://pizza.com"}]}]
            :photo-credits
            [{:_compound :photo-credit
              :images #{1 2}
              :photo-text
              "High neck shirt / 21000yen\nBlack merci pants / 34000yen"}
             {:_compound :photo-credit
              :images #{0 6 8}
              :photo-text "High neck shirt / 21000yen\n"}]}})

(defn form-component []
  (let [form-state (formic-field/prepare-all-fields form-schema)]
    (fn []
      [:div "Parent component"
       [:pre (with-out-str (pprint (formic-field/validate-all form-state)))]
       [:form
        [ff/formic-fields form-schema form-state]
        [:pre (with-out-str (pprint (formic-field/serialize form-state)))]
        [:pre (with-out-str (pprint form-schema))]]])))

(defn init []
  (reagent/render-component
   [form-component]
   (.getElementById js/document "container")))

(init)
