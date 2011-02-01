(ns tray-racer.core
  (:require [clojure.contrib.math :as m])
  (:use alex-and-georges.debug-repl)
  ;(:require [tray-racer.math :as mm])
  (:use tray-racer.math)
  (:import [tray-racer.math vec3]))

(defn -main [& args] 
  (println "all your traced rays are belong to us!"))


(defn i-like-conds [x]
{:pre [(instance? String x)]
 :post [(instance? Integer %)]}
  (println x)
  (+ 3 2))

(def x (vec3. 22 33 44))
(println (normalize x))






