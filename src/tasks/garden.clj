(ns tasks.garden
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def processed
  (atom #{}))

(defn add-dep [env dep]
  (update-in env [:dependencies] (fnil conj []) dep))

(defn ns-tracker-pod []
  (->> '[[ns-tracker "0.3.1"] [org.clojure/tools.namespace "0.2.11"]]
       (assoc (boot/get-env) :dependencies)
       pod/make-pod))

(defonce garden-pods
  (pod/pod-pool (add-dep (boot/get-env) '[garden "1.3.4"])
                :init (fn [pod] (pod/require-in pod 'garden.core))))

(deftask build-garden
  "compile garden"
  [o output-to PATH      str   "The output css file path relative to docroot"
   s styles-var SYM      sym   "The var containing garden rules"
   p pretty-print        bool  "Pretty print compiled CSS"
   v vendors VENDORS     [str] "Vendors to apply prefixes for"
   c css-prepend PREPEND [str] "Raw CSS from resources to be prepended to the output"
   a auto-prefix PREFIX  #{kw} "Properties to auto-prefix with vendor-prefixes"]

  (let [output-path (or output-to "main.css")
        css-var     styles-var
        ns-sym      (symbol (namespace css-var))
        tmp         (boot/tmp-dir!)
        out         (io/file tmp output-path)
        src-paths   (vec (boot/get-env :source-paths))
        ns-pod      (ns-tracker-pod)]
    (pod/with-eval-in ns-pod
      (require 'ns-tracker.core)
      (def cns (ns-tracker.core/ns-tracker ~src-paths)))
    (boot/with-pre-wrap fileset
      (let [initial (not (contains? @processed css-var))
            preamble (mapv #(.getPath (io/resource %)) css-prepend)]
        (when (or initial (some #{ns-sym} (pod/with-eval-in ns-pod (cns))))
          (let [c-pod (garden-pods :refresh)]
            (when initial (swap! processed conj css-var))
            (util/info "Compiling %s...\n" (.getName out))
            (io/make-parents out)
            (pod/with-eval-in c-pod
              (require '~ns-sym)
              (spit ~(.getPath out)
                    (garden.core/css {:pretty-print? ~pretty-print
                                      :vendors ~vendors
                                      :auto-prefix ~auto-prefix
                                      :preamble ~preamble} ~css-var))))))
      (-> fileset (boot/add-resource tmp) boot/commit!))))
