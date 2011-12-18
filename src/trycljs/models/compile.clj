(ns trycljs.models.compile
  (:require [cljs.compiler :as comp]))

(defn compile-expr [cljs-expr]
  "Write text to temp file, compile, and delete the input file."
  (comp/emits (comp/analyze {} (read-string cljs-expr))))

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)
    (catch AssertionError e e)))
