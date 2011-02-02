(ns tray-racer.math
  (:require [clojure.contrib.math :as m])
  (:use alex-and-georges.debug-repl))

(defn vec3? [v & vs]
  (let [v-t (and (vector? v)
                 (= (count v) 3)
                 (reduce #(and %1 %2) (map number? v)))]
    (if vs
      (and v-t (apply vec3? vs))
      v-t)))
; XXX better way to filter for truthiness?

(defn length [v] 
  {:pre [(vec3? v)]
   :post [(number? %)]}
  (let [len-squared (reduce + (map * v v))]
    (m/sqrt len-squared)))

(defn normalize [v] 
  {:pre [(vec3? v)]
   :post [(vec3? %)]}
  (let [l (/ 1.0 (length v))]
    (vec (map (partial * l) v)))) 

(defn dot [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(number? %)]}
  (reduce + (map * v1 v2)))

(defn cross [[x1 y1 z1 :as v1] [x2 y2 z2 :as v2]]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  [(* y1 (- z2 z1) y2) (* z1 (- x2 x1) z2) (* x1 (- y2 y1) x2)])

(defn add [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  (vec (map + v1 v2)))

(defn minus [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  (vec (map - v1 v2)))

; XXX can I do this with overloading?
(defn mul [v1 x]
  {:pre [(vec3? v1) (or (vec3? x) (number? x))]
   :post [(vec3? %)]}
  (if (vec3? x)
    (vec (map * v1 x))
    (vec (map (partial * x) v1))))










