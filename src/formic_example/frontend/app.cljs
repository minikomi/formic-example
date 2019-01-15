(ns formic-example.frontend.app
  (:require [reagent.core :as r]
            [ajax.core :as ajax]
            [ajax.formats :as ajax-fmt]
            [cljs-time.coerce :refer [to-long]]
            [cljs-time.core :as t]
            [cljs.pprint :refer [pprint]]
            [cljs.pprint]
            [formic.components.date-picker :as dp]
            [formic.components.imagemodal :as formic-imagemodal]
            [formic.components.quill :as quill]
            [formic.field :as formic-field]
            [formic.frontend :as formic-frontend]
            [formic.util :as u]
            [formic.validation :as fv]
            [goog.dom :as gdom]
            [struct.core :as st]
            ;; local
            [formic-example.frontend.color-picker :as color-picker]
            [formic-example.frontend.form-styles :as form-styles]
            [formic-example.frontend.page-template :as page-template]
            ))

(defn dev-setup []
  (if goog.DEBUG
    (do (enable-console-print!)
        (println "dev mode"))
    (set! *print-fn* (fn [& _]))))

;; custom validator example

(defn date-active? [d]
  (not
   (t/equal? d (t/today))))

(def validate-date
  {:message "Choose any date but today."
   :optional true
   :validate date-active?})

;; custom image modal example

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

;; Compound fields

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

(def captioned-image-field
  {:options {:collapsable false}
   :fields
   [{:id :image
     :type :formic-imagemodal
     :validation [st/required]
     :options imagemodal-options}
    {:id :caption
     :type :string}]})

(def paragraph-field
  {:fields
   [{:id :title
     :title "Title (optional)"
     :type :string}
    {:id :body
     :type :formic-quill
     :validation [quill/not-blank]}]})

(def gallery-field
  {:fields
   [{:id :images
     :flex [:captioned-image]}]})

(def compound-fields
  {:page page-details-field
   :captioned-image captioned-image-field
   :paragraph paragraph-field
   :gallery gallery-field})

;; form schema

(def form-schema
  {:id :test-form
   :components {:color-picker color-picker/component}
   :options {:compound {:collapsable true
                        :default-collapsed true}}
   :compound compound-fields
   :fields [{:id :page-data
             :compound :page}
            {:id :article-body
             :flex [:paragraph :gallery]}]
   :classes form-styles/combined})

;; pretty print serialized form state

(defn serialized [form-state]
  [:pre#serialized
   (with-out-str
     (cljs.pprint/pprint 
      (formic-field/serialize form-state)))])

;; some starting data

(def starting-values
{:page-data
 {:title-text "title text value",
  :hero-image
  "https://images.unsplash.com/photo-1546946663-ac4155bd8ec6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
  :date-created "2014-06-12",
  :title-type "wide",
  :title-color "#8ed1fc",
  :subtitle-text "rwararaw",
  :compound :page},
 :article-body
 [{:title "elephants are cool",
   :body
   [{:insert
     "Elephants are herbivorous and can be found in different habitats including "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Savanna"},
     :insert "savannahs"}
    {:insert ", forests, deserts, and "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Marsh"},
     :insert "marshes"}
    {:insert
     ". They prefer to stay near water. They are considered to be a "}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Keystone_species"},
     :insert "keystone species"}
    {:insert
     " due to their impact on their environments. Other animals tend to keep their distance from elephants while predators, such as "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Lion"},
     :insert "lions"}
    {:insert ", "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Tiger"},
     :insert "tigers"}
    {:insert ", "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Hyena"},
     :insert "hyenas"}
    {:insert ", and any "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Canidae"},
     :insert "wild dogs"}
    {:insert
     ", usually target only young elephants (or \"calves\"). Elephants have a "}
    {:attributes
     {:link
      "https://en.wikipedia.org/wiki/Fission%E2%80%93fusion_society"},
     :insert "fission–fusion society"}
    {:insert
     " in which multiple family groups come together to socialise. Females (\"cows\") tend to live in family groups, which can consist of one female with her calves or several related females with offspring. The groups are led by an individual known as the "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Matriarchy"},
     :insert "matriarch"}
    {:insert ", often the oldest cow.\n"}],
   :compound :paragraph}
  {:images
   [{:image
     "https://images.unsplash.com/photo-1546934164-73ef3631f62d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
     :caption "oh a nice elephant",
     :compound :captioned-image}
    {:image
     "https://images.unsplash.com/photo-1546919992-3b0ccfae06aa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
     :caption "cool, she's just standing there",
     :compound :captioned-image}],
   :compound :gallery}
  {:title "standing",
   :body
   [{:attributes {:bold true}, :insert "Standing"}
    {:insert ", also referred to as "}
    {:attributes {:bold true}, :insert "orthostasis"}
    {:insert ", is a "}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Human_position"},
     :insert "human position"}
    {:insert " in which the body is held in an upright (\""}
    {:attributes {:bold true}, :insert "orthostatic"}
    {:insert
     "\") position and supported only by the feet.\n\nAlthough seemingly static, the body rocks slightly back and forth from the "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Ankle"},
     :insert "ankle"}
    {:insert " in the "}
    {:attributes
     {:link
      "https://en.wikipedia.org/wiki/Anatomical_terms_of_location#Planes"},
     :insert "sagittal"}
    {:insert
     " plane. The sagittal plane bisects the body into right and left sides. The sway of quiet standing is often likened to the motion of an "}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Inverted_pendulum"},
     :insert "inverted pendulum"}
    {:insert "."}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Standing#cite_note-1"},
     :insert "[1]"}
    {:insert "\n"}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Standing_at_attention"},
     :insert "Standing at attention"}
    {:insert " is a military standing posture, as is "}
    {:attributes {:link "https://en.wikipedia.org/wiki/Stand_at_ease"},
     :insert "stand at ease"}
    {:insert
     ", but these terms are also used in military-style organisations and in some professions which involve standing, such as "}
    {:attributes
     {:link "https://en.wikipedia.org/wiki/Model_(person)"},
     :insert "modeling"}
    {:insert ". "}
    {:attributes {:italic true}, :insert "At ease"}
    {:insert
     " refers to the classic military position of standing with legs slightly apart, not in as formal or regimented a pose as standing at attention. In modeling, "}
    {:attributes {:italic true}, :insert "model at ease"}
    {:insert
     " refers to the model standing with one leg straight, with the majority of the weight on it, and the other leg tucked over and slightly around.\n"}],
   :compound :paragraph}]})

;; render form

(defn form-component [form-schema]
  (let [form-state (formic-field/prepare-state form-schema starting-values)]
    (fn [form-schema] 
      [:div "Parent component"
       [:form
        {:style {:z-index 1}}
        [formic-frontend/fields form-state]
        [:button
         {:on-click (fn [ev]
                      (.preventDefault ev)
                      (formic-field/touch-all! form-state)
                      (when-let [err (formic-field/validate-all form-state)]
                       (cljs.pprint/pprint err)
                       (formic-frontend/uncollapse
                        form-state (get-in err [:node :path]))
                       (formic-frontend/focus-error)))}
         "Validate all"]]
       [page-template/page
        (formic-field/serialize form-state)]
       ])))

(defn mount []
  (r/render-component
   [form-component form-schema]
   (.getElementById js/document "container")))

(defn init []
  (dev-setup)
  (mount))

(init)
