(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :checkouts '[[co.poyo/formic "0.1.0-SNAPSHOT"]]
 :dependencies '[;; pin deps
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [adzerk/boot-cljs          "2.1.4"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.5.2"      :scope "test"]
                 [pandeiro/boot-http        "0.8.3"      :scope "test"
                  :exclusions [org.clojure]]
                 [com.cemerick/piggieback   "0.2.2"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.13"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [funcool/struct "1.2.0"]
                 [co.poyo/formic "0.1.0-SNAPSHOT"]
                 [garden "1.3.4"]
                 [reagent "0.7.0"]])

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
         concat '[[cider/cider-nrepl "0.17.0-SNAPSHOT"]
                  [refactor-nrepl "2.4.0-SNAPSHOT"]])
  (swap! @(resolve 'boot.repl/*default-middleware*)
         concat '[cider.nrepl/cider-middleware
                  refactor-nrepl.middleware/wrap-refactor])
  (repl :server true))

(deftask build-styles []
  (comp
   (tasks.garden/build-garden :styles-var 'formic-example.styles.core/combined
                              :output-to "public/css/styles.css"
                              :pretty-print (get-env :debug false))))

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
   cljs {:optimizations (if (get-env :debug) :none :advanced)
         :compiler-options
         {:closure-defines {'goog.DEBUG (get-env :debug false)}
          :parallel-build true}})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (set-env! :debug true)
  (comp
   (set-options)
   (cider)
   (serve :dir "target/public")
   (watch)
   (cljs-repl)
   (reload)
   (build-frontend)
   (target)))
