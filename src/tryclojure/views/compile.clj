(ns tryclojure.views.compile
  (:use [noir.core :only [defpage]]
        [tryclojure.models.compile :only [compile-request]])
  (:require [noir.response :as resp]))

(defpage "/compile.json" {:keys [expr jsonp]}
  (let [compiled (compile-request expr)
        data (if (instance? Exception compiled)
               {:error (.getMessage compiled)}
               {:js compiled})]
    (if jsonp
      (resp/jsonp jsonp data)
      (resp/json data))))