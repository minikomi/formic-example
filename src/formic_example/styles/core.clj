(ns formic-example.styles.core
  (:require
   [garden.selectors :as gs]
   [garden.units :refer [em percent px]]))

(def image-modal-close
  [:.formic-image-modal-close
   {:display 'block
    :position 'fixed
    :top 0
    :right 0
    :padding (px 10)
    :font-size (px 20)
    :line-height (px 20)
    :color 'white}
   [:&:hover
    {:opacity 0.6}]])

(def image-modal-panel-select
  [:ul.formic-image-modal-panel-select
   {:display 'block
    :padding 0
    :width (percent 100)
    :border-bottom [[(px 1) "#444" 'solid]]
    :margin 0}
   [:li
    {:list-style 'none
     :vertical-align 'bottom
     :border 'none
     :display 'inline-block}
    [:a
     {:display 'block
      :padding [[(px 10) (px 30)]]
      :border-radius [[(px 4) (px 4) 0 0]]
      :font-size (px 12)
      :margin-bottom (px -1)
      :background "#fefefe"
      :border-right [[(px 1) 'solid "#444"]]
      :border-top [[(px 1) 'solid "#444"]]
      :border-bottom [[(px 1) 'solid "#444"]]
      :margin-left (px 5)}
     ["&:first-child"
      {:border-left [[(px 1) 'solid "#444"]]}]]
    [:&.active
     [:a
      {:border-bottom [[(px 1) 'solid 'white]]
       :background 'white}]]]])

(def image-modal-current
  [:.formic-image-modal-current
   {:display 'block
    :width (percent 50)
    :margin [[(px 20) 'auto]]}
   [:&:hover
    {:opacity 0.8}]
   [:img
    {:display 'block
     :width (percent 100)}]
   [:span
    {:display 'block
     :text-align 'left
     :padding-bottom (px 5)
     :width (percent 100)}]])

(def image-modal-search
  [:.formic-image-modal-search
     {:text-align 'center}
     [:input
      {:display 'inline-block
       :width (percent 60)
       :border [[(px 1) 'solid "#444"]]
       :padding (px 10)
       :font-size (px 14)
       :line-height (px 14)}]
     [:a
      {:display 'inline-block
       :border [[(px 1) 'solid "#666"]]
       :margin-left (px 2)
       :padding (px 10)
       :font-size (px 14)
       :line-height (px 14)}]])

(def image-modal-grid
  [:.formic-image-modal-grid
     {:display 'block
      :padding 0
      :margin [[(px 20) 0 0 0]]}
     [:li
      {:list-style 'none
       :display 'inline-block
       :box-sizing 'border-box
       :vertical-align 'top
       :height 'auto
       :width (percent 20)
       :text-align 'center}
      [:a
       {:display 'block
        :padding (px 5)}
       [:&:hover
        {:background "#ddd"}]]
      [:&.selected
       {:border [[(px 1) 'solid 'red]]}]
      [:img
       {:vertical-align 'top
        :display 'inline-block
        :height 'auto
        :width (percent 100)
        :cursor 'pointer}]]])

(def image-modal-paging
  [:.formic-image-modal-paging
   {:width (percent 100)
    :margin-top (px 20)}
   [:li
    {:display 'inline-block
     :text-align 'center
     :line-height (px 14)
     :font-size (px 14)
     :vertical-align 'top}]
   [:.formic-image-modal-prev
    :.formic-image-modal-next
    {:width (percent 20)
     :text-align 'center}
    [:.button
     {:color 'white
      :background "#444"
      :border [[(px 1) 'solid "#444"]]
      :border-radius (px 4)
      :padding [[(px 5) (px 20)]]
      :display 'inline-block}
     [:&:hover
      {:background 'white
       :color "#444"}]]]
   [:.formic-image-modal-page-number
    {:width (percent 60)}]])

(def image-modal
  [[:body.formic-image-modal-open
    {:overflow-y 'hidden}]
   [:.formic-image-field
    {:position 'relative}
    [:.formic-image-modal
     {:position 'fixed
      :top 0
      :bottom 0
      :right 0
      :left 0
      :z-index 1000
      :background "rgba(0,0,0,0.5)"}]
    [:.formic-image-modal-inner
     {:background 'white
      :box-sizing 'border-box
      :padding (px 30)
      :width (percent 90)
      :position 'absolute
      :overflow-y 'scroll
      :height (percent 90)
      :top (percent 5)
      :left (percent 5)}]
    image-modal-panel-select
    image-modal-close
    image-modal-current
    image-modal-search
    image-modal-grid
    image-modal-paging
    [:.dropzone
     {:width (percent 90)
      :margin-left (percent 5)
      :margin-top (px 30)}]]])

(def image-current
  [:.formic-image-open-modal
   {:display 'block
    :text-align 'center
    :cursor 'pointer
    :margin-left 'auto
    :margin-right 'auto
    :position 'relative}
   [:.formic-image-current
    {:height 'auto
     :width (percent 100)
     :display 'block}]
   [:.formic-image-open-modal-label-wrapper
    {:position 'absolute
     :width (percent 100)
     :top 0
     :left 0
     :opacity 0
     :transition-duration "0.2s"
     :transition-easing 'ease-out
     :height (percent 100)
     :background "rgba(0,0,0,0.4)"}
    [:&:hover
     {:opacity 1
      :transition-easing 'ease-out
      :transition-duration "0.2s"}]
    [:.formic-image-open-modal-label
     {:color 'white
      :top (percent 50)
      :position 'absolute
      :left 0
      :right 0
      :text-align 'center
      :font-size (px 14)
      :line-height (px 14)
      :margin-top (px -8)}]]])

(def datepicker
  [:.formic-date-picker
   {:position 'relative
    :width (percent 100)}
   [:.date-picker-table-wrapper
    {:border [['solid (px 1) "#e2e2e2"]]
     :z-index 100
     :background 'white
     :width (percent 100)
     :left (percent 0)
     :top 0
     :box-shadow [[0 (px 3) (px 13) "rgba(0,0,0,0.2)"]]
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
  [[:form
    {:width (percent 30)
     :position 'fixed
     :left 0
     :top 0
     :bottom 0
     :background "#e6e9ec"
     :overflow-x 'hidden
     :border-right [[(px 3) 'solid "#444"]]
     :padding (px 10)
     :overflow-y 'scroll}
    ]
   [:.formic-quill
    [:.formic-quill-editor-wrapper
     {:background 'white
      :width (percent 100)
      :display 'block}]]])

(def combined
  [[:*
    {:box-sizing 'border-box
     :appearance 'none}]
   [:body
    {:font-family ['YuGothic 'sans-serif]
     :font-size (px 14)}
    [:#serialized
     {:padding-left (percent 35)}]
    form
    image-modal
    datepicker]])
