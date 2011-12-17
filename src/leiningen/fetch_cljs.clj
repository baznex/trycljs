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

(def cslib (io/file "resources" "public" "javascript" "cslib"))

(defn ^String dpath
  "Return a directory's path, always ending in a slash."
  [^File f]
  (let [p (.getAbsolutePath f)]
    (if (.endsWith p "/") p (str p "/"))))

(defn fetch-cljs [project]
  (and
   (make-sure (.exists ^File comp-base)
     (println "Downloading ClojureScript compiler's installer")
     (and (sh-check "git" "clone" cljs-git (dpath comp-base))
          (sh-check "git" "checkout"
                    "d625dc2154ce8a06e9cc1c56f4b53dc811662062"
                    :dir comp-base)))

   (make-sure (.exists ^File gcls-base)
     (println "Bootstrapping ClojureScript compiler")
     (sh-check "./script/bootstrap" :dir comp-base))

   (let [cljs-base (io/file cslib "closure") ;; GClosure insists...
         cljs-load-dir (io/file cljs-base "main")
         cljs-loader (io/file cljs-load-dir "load.js")]
     (make-sure (.exists ^File cljs-loader)
        (println "Building CLJS client library for website")
        (sh-check "mkdir" "-p" (dpath cljs-load-dir))
        (sh-check "bin/cljsc" (dpath (io/file comp-base "src" "cljs"))
                  (str {:output-dir (dpath cljs-base)
                        :output-to (.getAbsolutePath cljs-loader)})
                  :dir comp-base)
        (sh-check "rm" "-rf" "--" (dpath (io/file cljs-base "goog")))))

   ;; Note: Compiling CLJS client lib brings along goog -- order is important

   (let [gcls-lib (io/file gcls-base "library")
         copy-test (io/file cslib "third_party" "closure" "goog" "deps.js")]
     (make-sure (.exists ^File copy-test)
       (println "Copying Google Closure libs into website")
       (and
        (sh-check "mkdir" "-p" (dpath (io/file cslib "closure")))
        (sh-check "rsync" "-a"
                  (dpath (io/file gcls-lib "closure" "css"))
                  (dpath (io/file cslib "closure" "css")))
        (sh-check "rsync" "-a"
                  (dpath (io/file gcls-lib "closure" "goog"))
                  (dpath (io/file cslib "closure" "goog")))
        (sh-check "rsync" "-a"
                  (dpath (io/file gcls-lib "third_party"))
                  (dpath (io/file cslib "third_party"))))))))

(defn clean-cljs [project]
  (sh-check "rm" "-rf" "--"
            (dpath comp-base)
            (dpath cslib)))

(prepend-tasks #'deps fetch-cljs)
(prepend-tasks #'clean clean-cljs)
