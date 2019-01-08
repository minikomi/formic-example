(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-time.coerce :refer [to-long]]
            [cljs-time.core :as t]
            [cljs.pprint :refer [pprint]]
            [formic.components.date-picker :as dp]
            [formic.components.google-map :as gm]
            [formic.components.quill :as quill]
            [formic.components.imagemodal :as imagemodal]
            [formic.field :as formic-field]
            [formic.frontend :as formic-frontend]
            [formic.util :as u]
            [formic.validation :as fv]
            [goog.dom :as gdom]
            [ajax.core :as ajax]
            [ajax.formats :as ajax-fmt]
            [reagent.core :as r]
            [struct.core :as st]
            [cljs.pprint]
            ))

(defn dev-setup []
  (if goog.DEBUG
    (do (enable-console-print!)
        (println "dev mode"))
    (set! *print-fn* (fn [& _]))))

(def images-cache (atom []))

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
     :choices {"photo" "Photo"}}
    {:id :hero-image
     :type :formic-imagemodal
     :options {:endpoints {:list "https://api.unsplash.com/photos/?client_id=8f062abdd94634c81701ddd1e02a62089396f1088b973b632d93ab45157e7c92&per_page=30"}
               :paging true
               :image->title
               (constantly false)
               :image->thumbnail
               (fn [img]
                 (str (get-in img ["urls" "small"])
                      "?w=100&h=100&fit=clamp"))
               :list-images-fn
               (fn [endpoints state]
                 (swap! state assoc
                        :mode :loading
                        :current-images nil)
                 (ajax/GET
                  (str (:list endpoints)
                       "&page=" (:current-page @state))
                  {:response-format
                   (ajax/ring-response-format {:format (ajax/json-response-format)})
                   :handler
                   (fn [resp]
                     (println (get-in resp [:headers "x-total"]))
                     (swap! state assoc
                            :mode :loaded
                            :next-page (when-let [total-str (get-in resp [:headers "x-total"])]
                                           (> (js/parseInt total-str)
                                              (* 30 (:current-page @state 1))))
                            :prev-page (> 0 (:current-page @state))
                            :current-images (:body resp)
                      )
                     )}))
               }}
    {:id :date-created
     :default (t/today)
     :type :formic-datepicker
     :validation [st/required validate-date]
     :options {:active? date-active?}
     }
    {:id :title-type
     :type :radios
     :choices {"wide" "Wide"
               "normal" "Normal"}
     :validation [st/required]}
    {:id :subtitle-text
     :type :string
     :validation []}
    {:id :issue-text
     :type :string
     :validation []}]
   :validation
   {:issue-text [[st/identical-to :subtitle-text]]}})

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
     :type :formic-google-map
     :autocomplete true
     :validation []}]})

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

(def photo-credit-field
  {:fields
   [{:id :photo-text
     :type :formic-quill
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
   :compound compound-fields
   :fields form-fields})

(defn serialized [form-state]
  [:pre
   (with-out-str
     (cljs.pprint/pprint 
      (formic-field/serialize form-state)))])

(def values 
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
     :photo-text "High neck shirt / 21000yen\n"}]})

(defn form-component [form-schema]
  (let [form-state (formic-field/prepare-state form-schema values)]
    (fn [form-schema] 
      [:div "Parent component"
       [:form
        [formic-frontend/fields form-state]]
       [serialized form-state]
       [:button
        {:on-click (fn [ev]
                     (.preventDefault ev)
                     (formic-field/touch-all! form-state)
                     (cljs.pprint/pprint (formic-field/validate-all form-state)))}]])))

(defn mount []
  (reagent/render-component
   [form-component form-schema]
   (.getElementById js/document "container")))

(defn init []
  (dev-setup)
  (mount))

(init)
