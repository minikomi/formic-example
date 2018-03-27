(set-env!
 :source-paths    #{"src/cljs"}
 :resource-paths  #{"resources"}
 :checkouts '[[co.poyo/formic "0.1.0-SNAPSHOT"]]
 :dependencies '[[adzerk/boot-cljs          "2.1.4"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.5.2"      :scope "test"]
                 [pandeiro/boot-http        "0.8.3"      :scope "test"
                  :exclusions [org.clojure]]
                 [com.cemerick/piggieback   "0.2.2"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.13"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [funcool/struct "1.2.0"]
                 [co.poyo/formic "0.1.0-SNAPSHOT"]
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.7.0"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask build
  "This task contains all the necessary steps to produce a build
   You can use 'profile-tasks' like `production` and `development`
   to change parameters (like optimizations level of the cljs compiler)"
  []
  (cljs))

(deftask run
  "The `run` task wraps the building of your application in some
   useful tools for local development: an http server, a file watcher
   a ClojureScript REPL and a hot reloading mechanism"
  []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none}
                 reload {:on-jsload 'formic-example.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))
