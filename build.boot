(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :checkouts '[[co.poyo/formic "0.1.3"]
              [co.poyo/formic-datepicker "0.1.0-SNAPSHOT"]
              [co.poyo/formic-imagemodal "0.1.0-SNAPSHOT"]
              [co.poyo/formic-quill "0.1.0-SNAPSHOT"]]
 :dependencies '[;; pin deps
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [adzerk/boot-cljs          "2.1.5"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.4.0"      :scope "test"]
                 [adzerk/boot-reload        "0.5.2"      :scope "test"]
                 [co.poyo/formic-google-map "0.1.0-SNAPSHOT"]
                 [pandeiro/boot-http        "0.8.3"      :scope "test"
                  :exclusions [org.clojure]]
                 [cider/piggieback          "0.3.9"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.13"     :scope "test"]
                 [nrepl "0.4.5" :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [funcool/struct "1.3.0"]
                 [cljs-ajax "0.7.5"]
                 [cljsjs/react-flip-move "3.0.1-1"]
                 ;; forms
                 [co.poyo/formic "0.1.3"]
                 [co.poyo/formic-imagemodal "0.1.0-SNAPSHOT"]
                 [co.poyo/formic-datepicker "0.1.0-SNAPSHOT"]
                 [co.poyo/formic-quill "0.1.0-SNAPSHOT"]
                 [co.poyo/delta-to-hiccup "0.1.0-SNAPSHOT"]
                 [cljsjs/react "16.6.0-0"]
                 [cljsjs/react-color "2.13.8-0"]
                 [garden "1.3.6"]
                 [reagent "0.8.1"]])

(require
 '[tasks.garden]
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask cider "CIDER profile" []
  (alter-var-root #'clojure.main/repl-requires conj
                  '[blooming.repl :refer [start! stop! restart!]])
  (require 'boot.repl)
  (swap! @(resolve 'boot.repl/*default-dependencies*)
         concat '[[cider/cider-nrepl "0.19.0"]
                  [refactor-nrepl "2.4.0"]])
  (swap! @(resolve 'boot.repl/*default-middleware*)
         concat '[cider.nrepl/cider-middleware
                  refactor-nrepl.middleware/wrap-refactor])
  (repl :server true))

(deftask build-styles []
  (comp
   (tasks.garden/build-garden :styles-var 'formic-example.styles.core/combined
                              :output-to "public/css/styles.css"
                              :pretty-print (boolean (get-env :debug false)))))

(deftask build-frontend
  "This task contains all the necessary steps to produce a build
   You can use 'profile-tasks' like `production` and `development`
   to change parameters (like optimizations level of the cljs compiler)"
  []
  (comp
   (build-styles)
   (cljs)))

(deftask set-options []
  (task-options!
   ;;frontend
   cljs {:optimizations (if (not= (get-env :debug) :debug)
                          :advanced
                          :none)
         :compiler-options
         {:pseudo-names (= (get-env :debug) :pseudo)
          :closure-defines {'goog.DEBUG (get-env :debug false)}
          :parallel-build true}})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (set-env! :debug :debug)
  (comp
   (set-options)
   (cider)
   (serve :dir "target/public" :port 3042)
   (watch)
   (cljs-repl)
   (reload)
   (build-frontend)
   (target)))

(deftask pseudo
  "Simple alias to run application in development mode"
  []
  (set-env! :debug :pseudo)
  (comp
   (set-options)
   (cider)
   (serve :dir "target/public" :port 3042)
   (watch)
   (cljs-repl)
   (reload)
   (build-frontend)
   (target)))

(deftask build []
  (comp
   (set-options)
   (build-frontend)
   (sift :include #{#"\.out" #".*\.edn$"} :invert true)
   (sift :include #{#"^public/"})
   (target)))
