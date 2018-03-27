(ns formic-example.app
  (:require [reagent.core :as reagent :refer [atom]]
            [struct.core :as st]
            [formic.validation :as fv]
            [formic.frontend :as ff]))

(def validate-banana
  {:message "must be same name"
   :optional true
   :validate (fn [banana-data]
               (println banana-data)
               (= (get-in banana-data [:first-name])
                  (get-in banana-data [:banana-login :first-name])))})

(def form-data
  {:id :test-form
   ;; compound fields
   :compound
   {:banana
    {:fields
     [{:id :first-name
       :type :string
       :validation [st/required]}
      {:id :first-name-confirm
       :type :string
       :validation [st/required]}
      {:id :last-name
       :type :string
       :validation [st/required]}]
     :validation
     [[:first-name-confirm [st/identical-to :first-name]]]}
    :login
    {:fields
     [{:id :first-name
       :type :string
       :validation [st/required]}
      {:id :last-name
       :type :string
       :validation [st/required]}]}}
   ;; form fields
   :fields
   [{:id :login
     :compound :banana}
    {:id :quill-test
     :type :quill}
    {:id :login2
     :compound :banana}]
  ;; values
   :values
   {
    :login
    {:first-name "aaa"
     :last-name "bbb"}
    :login2
    {:first-name "rewrer"
     :first-name-confirm "raaaa"
     :last-name "rearea"}}})

(defn form-component []
  [:div "Parent component"
   [:form [ff/formic-fields form-data]]])

(defn init []
  (reagent/render-component
   [form-component]
   (.getElementById js/document "container")))
