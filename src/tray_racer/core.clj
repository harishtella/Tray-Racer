(ns tray-racer.core)

(defn -main [& args] 
  (println "all your traced rays are belong to us!"))


(defn i-like-conds [x]
{:pre [(instance? String x)]
 :post [(instance? Integer x)]}
  (println x)
  (+ 3 2))

