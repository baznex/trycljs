(ns leiningen.fetch-cljs
  (:use [leiningen.core :only [prepend-tasks]]
        [leiningen.deps :only [deps]])
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]))

(defn proc-check
  "Given the result map from calling sh, either return true and go on our merry
way or print the non-zero exit code, stdout, and stderr and return false."
  [{:keys [out err exit]}]
  (if (zero? exit)
    true
    (do
      (println "Non-zero exit code:" exit)
      (println "stdout and stderr follow:")
      (println out)
      (println err)
      false)))

(defmacro make-sure
  "Ensure a condition is true, or take steps to rectify it: If the check returns
true, return true. Otherwise, execute the remaining forms and return the result.
"
  [check & otherwise]
  `(if ~check
     true
     (do ~@otherwise)))

(def cljs-git "git://github.com/clojure/clojurescript.git")

(defn fetch-cljs [project]
  (let [cljs (io/file "resources" "private" "cljs-compiler")
        closure (io/file cljs "closure")]
    (and
     (make-sure (.exists cljs)
       (println "Downloading ClojureScript compiler's installer (latest.)")
       (proc-check (sh/sh "git" "clone" cljs-git (.getAbsolutePath cljs))))
     (make-sure (.exists closure)
       (println "Bootstrapping ClojureScript compiler...")
       (proc-check (sh/sh "./script/bootstrap" :dir cljs))))))

(prepend-tasks #'deps fetch-cljs)
