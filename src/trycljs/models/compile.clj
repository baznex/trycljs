(ns trycljs.models.compile
  (:require [cljs.compiler :as comp]))

(defn read-str-all
  "read-string all forms, returning a possibly empty collection"
  [s]
  ;; The \n is to end any comment in the string
  (rest (read-string (str "(do " s " \n)"))))

(defn compile-expr [cljs-expr]
  "Write text to temp file, compile, and delete the input file."
  (binding [comp/*cljs-ns* 'cljs.user]
      (let [env {:ns (@comp/namespaces comp/*cljs-ns*)
                 :context :statement
                 :locals {}}
            forms (binding [*ns* (create-ns comp/*cljs-ns*)]
                    (read-str-all cljs-expr))
            ;; Wrapping in (#(last %&) ...) means that "1 2 3" will not be
            ;; optimized to empty statement. Wrapping in pr-str will *ideally*
            ;; give us a nice printout instead of [object Object].
            wrapped `(do ; (ns cljs.user)
                         (pr-str (#(last %&) ~@forms)))]
        (comp/emits (comp/analyze env wrapped)))))

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)
    (catch AssertionError e e)))
