(ns formic-example.styles.core
  (:require
   [garden.selectors :as gs]
   [garden.units :refer [em percent px]]))

(def field-flex
  [:.formic-flex
   [:.formic-flex-field
    {:position      'relative
     :margin        [[(percent 2) 'auto]]
     :background    "#f2f2f2"
     :width         (percent 98)
     :border        [[(px 1) 'solid "#e2e2e2"]]
     :box-shadow [[0 0 (px 13) "rgba(0,0,0,0.1)"]]
     :border-radius (px 6)
     :padding       (px 8)}
    [:h4
     {:margin    0
      :font-size (px 12)
      :padding   (px 6)}]
    [:.formic-flex-controls
     {:margin     0
      :padding    0
      :position   'absolute
      :right      (px 10)
      :top        (px 10)
      :text-align 'right}
     [:li
      {:list-style 'none
       :display    'inline-block}
      [:a
       {:display         'block
        :padding         (px 4)
        :font-size       (px 12)
        :line-height     (px 12)
        :text-decoration 'none
        :color           'black
        :margin-left     (px 2)
        :width           (px 22)
        :text-align      'center
        :border          [[(px 1) 'solid "#ccc"]]
        :background      'white}
       [:&.disabled
        {:cursor  'normal
         :opacity 0.2}]]]]]])

(def field-photo-credit
  [:.formic-compound.photo-credit
   [:.formic-checkboxes
    [:ul
     {:width (percent 90)
      :margin 0
      :padding 0
      :display 'inline-block}]
    [:li
     {:display      'inline-block
      :background   "#fff"
      :border       [[(px 1) 'solid "#ccc"]]
      :line-height  0
      :padding      (px 4)
      :margin-right (px 10)}
     [:img
      {:display 'inline-block
       :height  (px 40)}]
     [:input
      {:vertical-align 'top
       :margin-top     (px 14)
       :margin-right   (px 10)}]]]])

(def compound-field
  [:ul.formic-compound-fields
   {:margin  [[(px 10) 0]]
    :padding (px 10)}
   ["> li"
    {:list-style 'none
     :margin     [[(px 10) 0]]
     :padding    0}
    [:span.formic-input-title
     {:font-weight    'bold
      :vertical-align 'top
      :display        'inline-block
      :width (percent 10)
      :padding-bottom (px 5)
      :margin-bottom  (px 5)
      :font-size      (px 12)}]]])

(def datepicker
  [:.date-picker
   {:position 'relative
    :width (percent 100)}
   [:.date-picker-table-wrapper
    {:border [['solid (px 1) "#e2e2e2"]]
     :position 'absolute
     :z-index 100
     :background 'white
     :width (percent 40)
     :left (percent 10)
     :top (px 30)
     :box-shadow [[0 (px 3) (px 13) "rgba(0,0,0,0.1)"]]
     :padding (px 10)}]
   [:td
    {:user-select 'none}]
   [:td.next.active
    :td.prev.active
    {:cursor 'pointer
     :height (px 30)}
    [:&:hover
     {:background "#aaa"
      :color 'white}]]
   [:table.date-picker-table
    {:width (percent 100)
     :border-collapse 'collapse}
    [:select
     {:margin (px 4)}]
    [:tr.days
     [:td
      {:height (px 30)
       :background "#e2e2e2"}]]
    [:tr
     [:td
      {:text-align 'center
       :padding 0
       :width (percent (/ 100 7))
       :font-size (px 12)
       :line-height 0
       :border [[(px 1) 'solid "#ebebeb"]]}
      [:.wrapper
       {:display 'block
        :line-height (px 40)
        :vertical-align 'middle
        :width (percent 100)
        :height (percent 100)}]
      [:&.not-valid
       {:color "#e2e2e2"}]
      [:&.valid
       {:cursor 'pointer}
       [:&:hover
        [:.wrapper
         {:background "#f2f2f2"}]]]
      [:&.today
       {:font-weight 'bold
        :text-decoration 'underline}]
      [:.wrapper
       {:width (percent 100)
        :display 'inline-block}]
      [:&.selected.valid
       [:.wrapper
        {:background "#2f59a3"
         :color 'white}]]]]]])

(def form
  [:form
   {:width   (percent 90)
    :margin  [[0 'auto]]
    :padding (px 30)}
   [:div.formic-fields
    ["> div.formic-field"
     {:border-radius (px 6)
      :background    'white
      :margin-top    (px 30)
      :padding       0}
     ["> .formic-compound > h4.formic-compound-title"
      "> .formic-flex > h4.formic-flex-title"
      {:margin        0
       :padding       (px 10)
       :background    "#2f59a3"
       :color         'white
       :border-radius [[(px 6) (px 6) 0 0]]
       :border-bottom [[(px 1) 'solid "#e2e2e2"]]}]]]
   [:fieldset
    {:border  'none
     :padding 0
     :margin  0}]
   field-flex
   field-photo-credit
   compound-field
   datepicker
   [:.formic-google-map
    {:display 'inline-block
     :width (percent 90)}
    [:.map-wrapper
     [:.map-holder
      {:margin-top (px 10)}
      {:height (px 450)
       :width (percent 100)}]]
    [:.formic-lat-lng
     {:margin-top (px 10)}
     [:span
      {:display 'inline-block
       :font-size (px 12)
       :font-weight 'bold
       :margin [[(px 10) (px 10) 0 0]]}]
     [:input
      {:padding (px 5)
       :background "#fafafa"
       :margin-right (px 10)}]]
    [:.formic-auto-complete
     [:span
      {:display 'inline-block
       :font-size (px 12)
       :font-weight 'bold
       :width (percent 5)}]
     [:input
      {:display 'inline-block
       :margin-left (percent 2)
       :padding [[(px 5)]]
       :width (percent 93)}]]]
   [:textarea
    {:min-height (px 200)
     :min-width  (px 500)}]
   [:input
    [(gs/& (gs/attr :type := :text))
     {:border              [[(px 1) 'solid "#e2e2e2"]]
      :width               (percent 90)
      :display             'inline-block
      :vertical-align 'top
      "-webkit-appearance" 'none
      :padding             [[(px 8) (px 4)]]}]]])

(def combined
  [[:*
    {:box-sizing 'border-box
     :appearance 'none}]
   [:body
    {:background "#e6e9ec"
     :font-family ['YuGothic 'sans-serif]
     :font-size (px 14)}
    form]])
