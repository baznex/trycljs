(ns leiningen.fetch-cljs
  (:use [leiningen.core :only [prepend-tasks]]
        [leiningen.deps :only [deps]])
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]))

(def cljs-git "git://github.com/clojure/clojurescript.git")

(defn fetch-cljs [project]
  (let [cljs (io/file "resources" "private" "cljs-compiler")]
    (when-not (.exists cljs)
      (println "Downloading ClojureScript compiler's installer (latest.)")
      (sh/sh "git" "clone" cljs-git (.getAbsolutePath cljs))
      (println "Bootstrapping ClojureScript compiler...")
      (sh/sh "./script/bootstrap" :dir cljs))))

(prepend-tasks #'deps fetch-cljs)
