(ns formic-example.styles.core
  (:require
   [garden.selectors :as gs]
   [garden.units :refer [em percent px]]))

(def form
  [:form
   {:width (percent 90)
    :margin [[0 'auto]]
    :padding (px 30)}
   [:div.formic-fields
    ["> div.formic-field"
     {:border-radius (px 6)
      :background 'white
      :margin-top (px 30)
      :padding 0}
     ["> .formic-compound > h4.formic-compound-title"
      "> .formic-flex > h4.formic-flex-title"
      {:margin 0
       :padding (px 10)
       :background "#2f59a3"
       :color 'white
       :border-radius [[(px 6) (px 6) 0 0]]
       :border-bottom [[(px 1) 'solid "#e2e2e2"]]}]]]
   [:fieldset
    {:border 'none
     :padding 0
     :margin 0}]
   [:.formic-flex
    [:.formic-flex-field
     {:position 'relative
      :margin [[(percent 2) 'auto]]
      :background "#f2f2f2"
      :width (percent 98)
      :border [[(px 1) 'solid "#e2e2e2"]]
      :border-radius (px 6)
      :padding (px 8)}
     [:h4
      {:margin 0
       :font-size (px 10)
       :padding (px 6)}]
     [:.formic-flex-controls
      {:margin 0
       :padding 0
       :position 'absolute
       :right (px 10)
       :top (px 10)
       :text-align 'right}
      [:li
       {:list-style 'none
        :display 'inline-block}
       [:a
        {:display 'block
         :padding (px 4)
         :font-size (px 12)
         :line-height (px 12)
         :text-decoration 'none
         :color 'black
         :margin-left (px 2)
         :width (px 22)
         :text-align 'center
         :border [[(px 1) 'solid "#ccc"]]
         :background 'white}
        [:&.disabled
         {:display 'none}]]]]]]
   [:ul.formic-compound-fields
    {:margin [[(px 10) 0]]
     :padding (px 10)}
    ["> li"
     {:list-style 'none
      :margin [[(px 10) 0]]
      :padding 0}
     [:span.formic-input-title
      {:font-weight 'bold
       :display 'block
       :padding-bottom (px 5)
       :margin-bottom (px 5)
       :font-size (px 12)}]]]
   [:input
    [(gs/& (gs/attr :type := :text))
     {:border [[(px 1) 'solid "#e2e2e2"]]
      :width (percent 100)
      "-webkit-appearance" 'none
      :padding [[(px 8) (px 4)]]}]]])

(def combined
  [[:*
    {:box-sizing 'border-box
     :appearance 'none}]
   [:body
    {:background "#e6e9ec"
     :font-family ['YuGothic 'sans-serif]
     :font-size (px 14)}
    form]])
