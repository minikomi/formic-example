(ns formic-example.frontend.form-styles)

(def common-title-styles
  [:db :lh-copy :mt3 :mb1 :f7 :fw2 :pb1])

(def common-input-styles
  {:title     common-title-styles
   :input     [:ph1 :pv2 :input-reset :ba :bg-white :w-100 :f--focus]
   :err-input [:ph1 :pv2 :input-reset :ba :bg-washed-red :w-100 :red]
   :err-label [:w-100 :red :f5]})

(def select-styles
  {:title     [:db :fw6 :lh-copy :h1 :mv2 :f6]
   :input     [:f--select :ba :b--black :w-100 :ph1 :pv2 :bg-white :lh-solid :v-mid :f--focus]
   :err-input [:ph1 :pv2 :input-reset :ba :bg-washed-red :w-100 :red]
   :err-label [:w-100 :red :f4]})

(def radios-styles
  {:title        common-title-styles
   :input        [:dib :mr2 :v-top]
   :list         [:list :pa0]
   :item         [:dib :mt1]
   :label        [:f6 :pa1 :v-mid :l-copy :dib]
   :active-input [:pa2 :input-reset :ba :mr2 :v-mid :l-copy :active]})

(def compound-styles
  {:fieldset    [:ph2 :bn :sans-serif :bg-white]
   :title       [:f6 :db :fw6 :black :pa0 :mv1 :w-100 :fl]
   :fields-list [:list :pt0 :ph0 :pb3 :mv0 :w-100 :fl]
   :fields-item [:pb2]
   :errors-list [:list]
   :errors-item []
   :error       []})

(def flex-add-styles
  {:list    [:list :db :w-100 :bg-white :pa2 :mt0 :mt2]
   :item    [:dib :mr2 :mt1 :mb1 :pa0]
   :button  [:f6 :no-underline :white
             :dib
             :b--blue :ba :bg-blue :pa2 :br2
             :hover-bg-white :hover-blue]})

(def common-button-styles
  [:link :mr2  :dib :ba :br1 :ph0 :pv1 :tc :w2 :v-mid])

(def flex-controls-styles
  {:wrapper              [:list :absolute :top-negative :right-0]
   :move                 [:dib :ma0]
   :move-disabled        [:dib]
   :move-button          (into common-button-styles [:blue :b--blue :hover-bg-light-gray])
   :move-button-disabled (into common-button-styles [:light-gray :b--light-gray])
   :delete               [:dib :ma0]
   :delete-button        (into common-button-styles [:red :b--red :br1 :hover-bg-light-gray])})

(def flex-styles
  {:fieldset       [:pt3 :ph0 :bn]
   :err-fieldset   [:pt3 :ba :b--red]
   :fields-wrapper [:w-100 :dib]
   :title          [:f4 :dib :fw6 :black :pa0 :mv1 :w-100]
   :add            flex-add-styles
   :fields-list    [:list :db :w-100 :pa0 :mh0 :mt2 :mb0 :relative]
   :fields-item    [:pt1 :mb0 :pa0 :mt2 :relative :bg-white]
   :controls       flex-controls-styles
   :err-wrapper    []
   :err-label      [:w-100 :red :f4 :mt0]})

(def buttons-styles
  {:list
   [:list :pa0 :w100]
   :item
   [:dib :pa0 :mr2]
   :button
   {:save
    [:f6 :no-underline :white
     :b--green :ba :bg-green
     :pv2 :ph4 :br2
     :grow :pointer :dib
     :hover-bg-white :hover-green]
    :delete
    [:f6 :no-underline :white
     :b--red :ba :bg-red
     :pv2 :ph4 :br2
     :grow :pointer :dib
     :hover-bg-white :hover-red]
    :cancel
    [:f6 :no-underline :white
     :b--blue :ba :bg-blue
     :grow :pointer :dib
     :pv2 :ph4 :br2
     :hover-bg-white :hover-blue]}})

(def combined
  {:compound compound-styles
   :flex     flex-styles
   :fields
   {:string     common-input-styles
    :number     common-input-styles
    :select     select-styles
    :radios radios-styles
    :formic-imagemodal common-input-styles
    :formic-datepicker common-input-styles}
   :buttons buttons-styles})
