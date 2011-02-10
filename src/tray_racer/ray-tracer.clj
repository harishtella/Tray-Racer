(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:import [tray-racer.scene Ray])
  (:use clojure.stacktrace))

(s/init-scene)
(p/pprint s/Scene)
  

(defn get-prim-hit [ray] 
  (map #(s/intersect % ray) @s/Scene))

(filter #(number? (second %)) ---)
(sort-by second < ---)

(defn ray-point [ray t]
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))

(defn calc-color [hit-point prim]
  (let [n (normal prim hit-point)]
    (loop [color [0 0 0] lights (get-lights)]
      (let [light (first lights)
            l (v/- (:center light) (:center prim))
            l (v/normalize l)
            dot (v/dot n l)
            diff (* dot (v/m :diffuse prim))
            c (reduce v/* 
                      diff 
                      (v/m :color light) 
                      (v/m :color prim))]
        (recur (v/+ color c) (next lights))))))

