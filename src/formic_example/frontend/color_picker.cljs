(ns formic-example.frontend.color-picker
  (:require
   [formic.components.inputs :as inputs]
   [cljsjs.react]
   [goog.object :as gobj]
   [cljsjs.react-color]
   ))

(def twitter-picker (js/React.createFactory js/ReactColor.TwitterPicker))

(defn component []
  (fn [{:keys [id touched value err options] :as f}]
    [inputs/common-wrapper f
     [twitter-picker
      #js {:color #js {:hex @value}
           :onChangeComplete
           #(reset! value (gobj/get % "hex"))}]]))
