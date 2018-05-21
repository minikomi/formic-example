(ns formic-example.frontend.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-time.coerce :refer [to-long]]
            [cljs-time.core :as t]
            [cljs.pprint :refer [pprint]]
            [formic.components.date-picker :as dp]
            [formic.components.quill :as quill]
            [formic.field :as formic-field]
            [formic.frontend :as ff]
            [formic.util :as u]
            [formic.validation :as fv]
            [goog.dom :as gdom]
            [reagent.core :as r]
            [struct.core :as st]
            ))

(def geocoder (google.maps.Geocoder.))

(defn map->lat-lng [{:keys [lat lng]}]
  (when (and lat lng)
    (js/google.maps.LatLng. lat lng)))

(defn geocode-position [current-value]
  (.geocode geocoder
            (clj->js {:location (map->lat-lng @current-value)})
            (fn [results status]
              (when (= status "OK")
                (swap! current-value assoc
                       :address (.-formatted_address (first results)))))))

(defn google-map [{:keys [current-value touched err] :as f}]
  (let [state (r/atom :init)
        map (r/atom nil)
        map-holder-el (r/atom nil)
        autocomplete-input-el (r/atom nil)
        autocomplete (r/atom nil)
        address-val (r/atom "")
        marker (r/atom nil)
        update-on-event (fn [ev]
                          (reset! touched true)
                          (.stop ev)
                          (swap! current-value
                                 assoc
                                 :lat (ev.latLng.lat)
                                 :lng (ev.latLng.lng))
                          (geocode-position current-value))]
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
                   :lng (.. pos -coords -longitude))
            (geocode-position current-value))))
       (r/track! (fn []
                   (when (and @map
                              @marker
                              (:lat @current-value)
                              (:lng @current-value))
                     (let [latlng (js/google.maps.LatLng.
                                   (:lat @current-value)
                                   (:lng @current-value))]
                       (.panTo @map latlng)
                       (.setPosition @marker latlng))))))
     :component-did-mount
     (fn [this _]
       ;; create map
       (reset! map
               (js/google.maps.Map.
                @map-holder-el
                (clj->js {:center (js/google.maps.LatLng. 35.6895 139.6917)
                          :zoom 12})))
       ;; create marker
       (reset! marker
        (js/google.maps.Marker.
         (clj->js {:center (js/google.maps.LatLng. 35.6895 139.6917)
                   :map @map
                   :draggable true
                   :animation js/google.maps.Animation.DROP
                   })))
       ;; update on marker drag
       (js/google.maps.event.addListener @marker "dragend" update-on-event)
       ;; reset marker position on map click
       (js/google.maps.event.addListener @map "click" update-on-event)
       ;; create autocomplete
       (reset! autocomplete (google.maps.places.Autocomplete. @autocomplete-input-el (clj->js {:types ["geocode" "establishment"]})))
       (.bindTo @autocomplete "bounds" @map)
       ;; update on autocomplete
       (.addListener @autocomplete
                     "place_changed"
                     (fn []
                       (reset! touched true)
                       (if-let [place (.getPlace @autocomplete)]
                         (when place.geometry
                           (if place.geometry.viewport
                             (.fitBounds @map place.geometry.viewport)
                             (do
                               (.setCenter @map place.geometry.location)
                               (.setZoom @map 17)))
                           (swap! current-value assoc
                                  :address (.. @autocomplete-input-el -value)
                                  :lat (place.geometry.location.lat)
                                  :lng (place.geometry.location.lng)))
                         (swap! current-value assoc :address :not-found))))
       (reset! state :active))
     :reagent-render
     (fn [{:keys [current-value err] :as f}]
      [:div
       [:span.formic-input-title (u/format-kw (:id f))]
       [:div.formic-google-map
        [:div.map-wrapper
         [:label.formic-auto-complete
          [:span "Address:"]
          [:input
           {:value (case (:address @current-value)
                     :not-found ""
                     nil ""
                     (:address @current-value))
            :on-change #(swap! current-value assoc :address (.. % -target -value))
            :ref (fn [el] (reset! autocomplete-input-el el))}]
          (when (= :not-found (:address @current-value))
            [:h4.not-found "Not Found."])]
         [:div.map-holder
          {:ref (fn [el] (reset! map-holder-el el))}]
         [:label.formic-lat-lng
          [:span "Lat:"]
          [:input {:type 'number
                   :step 0.0001
                   :min 0
                   :on-change (fn [ev]
                                (reset! touched true)
                                (when-let [v (not-empty (.. ev -target -value))]
                                  (swap! current-value assoc :lat (js/parseFloat v))
                                  (geocode-position current-value)))
                   :value (if-let [lat (:lat @current-value)]
                            (.toPrecision lat 8)
                            "")}]]
         [:label.formic-lat-lng
          [:span "Long:"]
          [:input {:type 'number
                   :step 0.0001
                   :min 0
                   :on-change (fn [ev]
                                (reset! touched true)
                                (when-let [v (not-empty (.. ev -target -value))]
                                  (swap! current-value assoc :lng (js/parseFloat v))
                                  (geocode-position current-value)))
                   :value (if-let [lat (:lng @current-value)]
                            (.toPrecision lat 8)
                            "")}]]]]])})))


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
     :validation []}]})

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
     :type :lat-lng
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
                :lat-lng google-map
                :quill quill/quill
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
