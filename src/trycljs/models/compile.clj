(ns trycljs.models.compile
  (:require [cljs.compiler :as comp]))

(defn compile-expr [cljs-expr]
  "Write text to temp file, compile, and delete the input file."
  (binding [comp/*cljs-ns* 'cljs.user]
      (let [env {:ns (@comp/namespaces comp/*cljs-ns*)
                 :context :statement
                 :locals {}}]
        (comp/emits (comp/analyze env (read-string cljs-expr))))))

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)
    (catch AssertionError e e)))
