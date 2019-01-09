(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-time.coerce :refer [to-long]]
            [cljs-time.core :as t]
            [cljs.pprint :refer [pprint]]
            [formic.components.date-picker :as dp]
            [formic.components.google-map :as gm]
            [formic.components.quill :as quill]
            [formic.components.imagemodal :as formic-imagemodal]
            [formic.field :as formic-field]
            [formic.frontend :as formic-frontend]
            [formic.util :as u]
            [formic.validation :as fv]
            [goog.dom :as gdom]
            [ajax.core :as ajax]
            [ajax.formats :as ajax-fmt]
            [reagent.core :as r]
            [struct.core :as st]
            [formic-example.frontend.form-styles :as form-styles]
            [cljs.pprint]
            [formic-example.frontend.page-template :as page-template]
            [formic-example.frontend.color-picker :as color-picker]
            ))

(defn dev-setup []
  (if goog.DEBUG
    (do (enable-console-print!)
        (println "dev mode"))
    (set! *print-fn* (fn [& _]))))

(def url-regex
  #"https?://(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")

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

(defn list-images-fn [endpoints state]
  (swap! state assoc
         :mode :loading
         :current-images nil)
  (let [endpoint (if (:search-str @state)
                   (str (:search endpoints)
                        "&page=" (inc (:current-page @state 0))
                        "&query=" (:search-str @state))
                   (str (:list endpoints) "&page=" (inc (:current-page @state 0))))]
    (ajax/GET endpoint
              {:response-format
               (ajax/ring-response-format {:format (ajax/json-response-format)})
               :error-handler (formic-imagemodal/default-error-handler state)
               :handler
               (fn [resp]
                 (swap! state assoc
                        :mode :loaded
                        :next-page
                        (when-let [total-str (get-in resp [:headers "x-total"])]
                          (> (js/parseInt total-str)
                             (* 30 (inc (:current-page @state 0)))))
                        :prev-page
                        (> 0 (:current-page @state))
                        :current-images
                        (mapv
                         #(get-in % ["urls" "raw"])
                         (if-let  [results (get-in resp [:body "results"])]
                           results
                           (:body resp)))))})))

(def imagemodal-options
  {:endpoints {:list "https://api.unsplash.com/photos/?client_id=8f062abdd94634c81701ddd1e02a62089396f1088b973b632d93ab45157e7c92&per_page=30"
               :search "https://api.unsplash.com/search/photos/?client_id=8f062abdd94634c81701ddd1e02a62089396f1088b973b632d93ab45157e7c92&per_page=30"}
   :paging true
   :search true
   :image->title
   (constantly false)
   :image->src
   (fn [img]
     (str img
          "&w=300&h=300&fit=clamp"))
   :image->thumbnail
   (fn [img]
     (str img
          "&w=300&h=300&fit=clamp"))
   :list-images-fn list-images-fn})

(def page-details-field
  {:fields
   [{:id :title-text
     :type :string
     :validation [st/required]}
    {:id :hero-image
     :type :formic-imagemodal
     :options imagemodal-options}
    {:id :date-created
     :default (t/today)
     :type :formic-datepicker
     :validation [st/required validate-date]
     :options {:active? date-active?}}
    {:id :title-type
     :type :radios
     :choices {"normal" "Normal"
               "wide" "Wide"}
     :validation [st/required]}
    {:id :title-color
     :type :color-picker}
    {:id :subtitle-text
     :label "Subtitle (optional)"
     :type :string
     :validation []}]})

(def quill-required
  {:message "Required"
   :optional true
   :validate (fn [v] (not-empty (:txt v)))})

(def compound-fields
  {:page page-details-field
   :captioned-image {:fields
                     [{:id :image
                       :type :formic-imagemodal
                       :validation [st/required]
                       :options imagemodal-options}
                      {:id :caption
                       :type :string}]}
   :paragraph {:fields
               [{:id :title
                 :title "Title (optional)"
                 :type :string}
                {:id :body
                 :type :formic-quill
                 :validation [quill-required]}]}
   :gallery {:fields
             [{:id :images
               :flex [:captioned-image]}]}})

(def form-fields
  [{:id :page-data
    :compound :page}
   {:id :article-body
    :flex [:paragraph :gallery]}])

(def form-schema
  {:id :test-form
   :components {:color-picker color-picker/component}
   :compound compound-fields
   :fields form-fields
   :classes form-styles/combined})

(defn serialized [form-state]
  [:pre#serialized
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
       [page-template/page
        (formic-field/serialize form-state)]
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
