(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [struct.core :as st]
            [formic.validation :as fv]
            [formic.components.quill :as quill]
            [cljs.pprint :refer [pprint]]
            [formic.field :as formic-field]
            [reagent.core :as r]
            [formic.frontend :as ff]))

(def url-regex #"https?://(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")

(def validate-url
  {:message "有効なURLを入力してください"
   :optional true
   :validate (fn [txt]
               (or
                (empty? txt)
                (re-matches url-regex txt)))})

(def page-details-field
  {:fields
   [{:id :title-text
     :type :string
     :validation [st/required]}
    {:id      :page-type
     :type    :radios
     :default "photo"
     :options {"photo" "Photo"}}
    {:id :title-type
     :type :radios
     :default "wide"
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
   :serializers {:quill quill/serializer}
   :values {:page-data
            {:page-type "photo"
             :title-text "title text value"
             :title-type "normal"
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
    [:div "Parent component"
     [:pre (with-out-str (pprint form-state))]
     [:form
      [ff/formic-fields form-schema form-state]]]))

(defn init []
  (reagent/render-component
   [form-component]
   (.getElementById js/document "container")))

(init)
