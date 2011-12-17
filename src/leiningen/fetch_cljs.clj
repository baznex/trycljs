(ns leiningen.fetch-cljs
  (:use [leiningen.core :only [prepend-tasks]]
        [leiningen.deps :only [deps]]
        [leiningen.clean :only [clean]])
  (:import (java.io File))
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]))

(defn sh-check
  "Given the result map from calling sh with args, either return true and go on
our merry way or print the non-zero exit code, stdout, and stderr and return
false."
  [& args]
  (let [{:keys [out err exit]} (apply sh/sh args)]
    (if (zero? exit)
      true
      (do
        (println "Non-zero exit code:" exit)
        (println "stdout and stderr follow:")
        (println out)
        (println err)
        false))))

(defmacro make-sure
  "Ensure a condition is true, or take steps to rectify it: If the check returns
true, return true. Otherwise, execute the remaining forms and return result.
If the result is true, run the check again. If the condition is still not met,
throw exception instead of returning."
  [check & otherwise]
  `(or ~check
       (if-let [ret# (do ~@otherwise)]
         (if ~check
           ret#
           (throw (Exception. (str "Condition still not met: " '~check))))
         false)))

(def cljs-git "git://github.com/clojure/clojurescript.git")

(def comp-base (io/file "resources" "private" "cljs-compiler"))
(def gcls-base (io/file comp-base "closure"))
(def gcls-lib (io/file gcls-base "library" "closure"))

(def pub-cljs-js (io/file "resources" "public" "javascript" "cljs"))
(def gcls-lib-tgt (io/file pub-cljs-js "closure"))
(def gcls-copy-test (io/file gcls-lib-tgt "goog" "base.js"))

(defn ^String dpath
  "Return a directory's path, always ending in a slash."
  [^File f]
  (let [p (.getAbsolutePath f)]
    (if (.endsWith p "/") p (str p "/"))))

(defn fetch-cljs [project]
  (and
   (make-sure (.exists ^File comp-base)
     (println "Downloading ClojureScript compiler's installer.")
     (and (sh-check "git" "clone" cljs-git (dpath comp-base))
          (sh-check "git" "checkout"
                    "d625dc2154ce8a06e9cc1c56f4b53dc811662062"
                    :dir comp-base)))
   (make-sure (.exists ^File gcls-base)
     (println "Bootstrapping ClojureScript compiler...")
     (sh-check "./script/bootstrap" :dir comp-base))
   (make-sure (.exists ^File gcls-copy-test)
     (println "Copying ClojureScript client libs into website")
     (and
      (sh-check "mkdir" "-p" (dpath gcls-lib-tgt))
      (sh-check "rsync" "-a"
                (dpath (io/file gcls-lib "css"))
                (dpath (io/file gcls-lib-tgt "css")))
      (sh-check "rsync" "-a"
                (dpath (io/file gcls-lib "goog"))
                (dpath (io/file gcls-lib-tgt "goog")))))))

(defn clean-cljs [project]
  (sh-check "rm" "-rf" "--"
            (dpath comp-base)
            (dpath pub-cljs-js)))

(prepend-tasks #'deps fetch-cljs)
(prepend-tasks #'clean clean-cljs)
