(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:use clojure.stacktrace))

(def p p/pprint)

(s/init-scene)
(p/pprint s/Scene)

(p/pprint (get-prim-hit (Ray. [-5.5 -0.5 0] [0 0 1])))
(print-cause-trace *e 5)

(def rrr (Ray. [-5.5 -0.5 0] [0 0 1]))
(def bbb (get-prim-hit rrr))   

;; XXX error with core.r
(calc-color (ray-point rrr (second bbb)) (first bbb))

;; dir must not be [0 0 0]  
(defrecord Ray [orig dir])
(defn ray-point [ray t]
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))

(defn get-prim-hit [ray] 
  (->>
    @s/Scene
    (map #(s/intersect % ray))
    (filter #(number? (second %)))
    (sort-by second <)
    (first)))

(defn calc-color [hit-point prim]
  (let [n (s/get-normal prim hit-point)]
    (loop [color [0 0 0] lights (s/get-lights)]
      (let [light (first lights)
            l (v/- (:center light) (:center prim))
            l (v/norm l)
            dot (v/dot n l)
            diff (* dot (s/m :diffuse prim))
            c (reduce v/* 
                      diff 
                      (s/m :color light) 
                      (s/m :color prim))]
        (recur (v/+ color c) (next lights))))))

