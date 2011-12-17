(ns trycljs.models.compile
  (:import (java.io File))
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [cljs.compiler :as comp]))

(def compiler (io/file "resources" "private" "cljs-compiler" "bin" "cljsc"))

(defn compile-expr [cljs]
  "Write text to temp file, compile, and delete the input file."
  (let [tin (File/createTempFile "trycljs-" ".cljs")
        tout (File/createTempFile "trycljs-" ".js")]
    (try
      (spit tin cljs)
      ;; Because CLJS compiler will not write to existing file, we have to
      ;; delete it first. This is not secure!
      (.delete tout)
      (comp/compile-file tin tout)
      (slurp tout)
      (finally (.delete tin)
               (.delete tout)))))

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)))
