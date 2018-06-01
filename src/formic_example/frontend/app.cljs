(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-time.coerce :refer [to-long]]
            [cljs-time.core :as t]
            [cljs.pprint :refer [pprint]]
            [formic.components.date-picker :as dp]
            [formic.components.google-map :as gm]
            [formic.components.quill :as quill]
            [formic.field :as formic-field]
            [formic.frontend :as ff]
            [formic.util :as u]
            [formic.validation :as fv]
            [goog.dom :as gdom]
            [reagent.core :as r]
            [struct.core :as st]
            ))

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
     :validation []}]
   :validation
   {:subtitle-text [[st/identical-to :issue-text]]}})

(def name-field
  {:fields
   [{:id :name
     :type :string
     :validation [st/required]}
    {:id :url
     :type :string
     :validation [validate-url]}]})

(def geo-field
  {:fields
   [{:id :name
     :type :string
     :validation [st/required]}
    {:id :location
     :type :google-map
     :autocomplete true
     :validation []}]
   })

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
     :type :quill
     :validation [st/required]}]})

(def compound-fields
  {:page page-details-field
   :name name-field
   :location geo-field
   :credit credit-field
   :photo-credit photo-credit-field})

(def form-fields
  [{:id :page-data
    :compound :page}
   {:id :locations
    :flex [:location]}
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
                 :quill quill/DEFAULT_SERIALIZER
                 }
   :parsers {
             :date dp/DEFAULT_PARSER
             }
   :components {
                :date dp/date-picker
                :google-map gm/google-map
                :quill quill/quill
                }})

(defn serialized [form-state]
  [:pre (with-out-str (pprint (formic-field/serialize form-state)))])

(defn form-component []
  (let [form-state (formic-field/prepare-state
                    form-schema
                    {:page-data
                     {:page-type "photo"
                      :title-text "title text value"
                      :title-type "normal"
                      :date-created "2014-06-12"
                      :subtitle-text nil
                      :issue-text nil}
                     :credits
                     [{:compound :credit
                       :role "hair"
                       :names
                       [{:compound :name :name "john" :url "http://google.com"}
                        {:compound :name :name "kaneko" :url "http://pizza.com"}]}]
                     :photo-credits
                     [{:compound :photo-credit
                       :images #{1 2}
                       :photo-text
                       "High neck shirt / 21000yen\nBlack merci pants / 34000yen"}
                      {:compound :photo-credit
                       :images #{0 6 8}
                       :photo-text "High neck shirt / 21000yen\n"}]})]
    (fn []
      [:div "Parent component"
       [:pre (with-out-str (pprint @form-state))]
       [:form
        [ff/formic-fields form-schema form-state]
        ;; [serialized form-state]
        ]])))

(defn init []
  (reagent/render-component
   [form-component]
   (.getElementById js/document "container")))

(init)
