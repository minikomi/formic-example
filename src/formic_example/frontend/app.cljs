(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [struct.core :as st]
            [formic.validation :as fv]
            [formic.components.quill :as quill]
            [formic.frontend :as ff]))

(def url-regex #"https?://(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")

(def validate-url
  {:message "有効なURLを入力してください"
   :optional true
   :validate (fn [txt]
               (or
                (empty? txt)
                (re-matches url-regex txt)))})

(def page-field
  {:fields
   [{:id      :page-type
     :type    :radios
     :default "photo"
     :options {"photo" "Photo"}}
    {:id :title-text
     :type :string
     :validation [st/required]}
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
     :type :string
     :validation [st/required]}
    {:id :names
     :flex [:name]}]})

(def compound-fields
  {:page page-field
   :name name-field
   :credit credit-field})

(def form-fields
  [{:id :page-data
    :compound :page}
   {:id :credits
    :flex [:credit]}
   ])

(def form-data
  {:id :test-form
   ;; compound fields
   :compound compound-fields
   ;; form fields
   :fields form-fields
   ;; serializers
   :serializers {:quill quill/serializer}
   })

(defn form-component []
  [:div "Parent component"
   [:form [ff/formic-fields form-data]]])

(defn init []
  (reagent/render-component
   [form-component]
   (.getElementById js/document "container")))

(init)
