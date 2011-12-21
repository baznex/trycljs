(ns trycljs.models.compile
  (:require [cljs.compiler :as comp]))

(defn compile-expr [cljs-expr]
  "Write text to temp file, compile, and delete the input file."
  (binding [comp/*cljs-ns* 'cljs.user]
      (let [env {:ns (@comp/namespaces comp/*cljs-ns*)
                 :context :statement
                 :locals {}}
            ;; Wrapping in (#(last %&) ...) means that "1 2 3" will not be
            ;; optimized to empty statement. Wrapping in pr-str will *ideally*
            ;; give us a nice printout instead of [object Object].
            wrapped (str "(clojure.core/pr-str (#(clojure.core/last %&) "
                         cljs-expr
                         " ))")]
        (comp/emits (comp/analyze env (read-string wrapped))))))

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)
    (catch AssertionError e e)))
