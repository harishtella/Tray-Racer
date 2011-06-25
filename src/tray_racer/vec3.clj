(ns tray-racer.vec3
  (:require [clojure.contrib.math :as m])
  (:refer-clojure :rename {+ cc+ - cc- * cc*})
  (:use alex-and-georges.debug-repl))

(defn vec3? [v & vs]
  (let [v-t (and (vector? v)
                 (= (count v) 3)
                 (reduce #(and %1 %2) (map number? v)))]
    (if vs
      (and v-t (apply vec3? vs))
      v-t)))
; TODO: is there a better way to filter for truthiness?

(defn len [v] 
  {:pre [(vec3? v)]
   :post [(number? %)]}
  (let [len-squared (reduce cc+ (map cc* v v))]
    (m/sqrt len-squared)))

(defn norm [v] 
  {:pre [(vec3? v)]
   :post [(vec3? %)]}
  (let [l (/ 1.0 (len v))]
    (vec (map (partial cc* l) v)))) 

(defn dot [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(number? %)]}
  (reduce cc+ (map cc* v1 v2)))

(defn cross [[x1 y1 z1 :as v1] [x2 y2 z2 :as v2]]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  [(cc* y1 (cc- z2 z1) y2) (cc* z1 (cc- x2 x1) z2) (cc* x1 (cc- y2 y1) x2)])

(defn + [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  (vec (map cc+ v1 v2)))

(defn - [v1 v2]
  {:pre [(vec3? v1 v2)]
   :post [(vec3? %)]}
  (vec (map cc- v1 v2)))

(defn * [a b]
  {:pre [(or (vec3? a) (number? a)) 
         (or (vec3? b) (number? b))
         (not (and (number? a) (number? b)))]
   :post [(vec3? %)]}
  (cond 
    (vec3? a b)
    (vec (map cc* a b))
    (number? a)
    (vec (map (partial cc* a) b))
    (number? b)
    (vec (map (partial cc* b) a))))





