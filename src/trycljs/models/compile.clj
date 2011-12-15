(ns trycljs.models.compile)

(defn compile-expr [cljs]
  cljs ;; for now, just echo
  )

(defn compile-request [expr]
  "Given a CLJS string, produce either a JS string or an Exception."
  (try
    (compile-expr expr)
    (catch Exception e e)))