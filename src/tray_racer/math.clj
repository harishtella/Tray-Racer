(ns tray-racer.math
  (:require [clojure.contrib.math :as m])
  (:use alex-and-georges.debug-repl))

(defprotocol vec-ops
             (v [this])
             (normalize [this])
             (length [this])
             (sqr-length [this])
             (dot [this other])
             (cross [this other])
             (add [this other])
             (minus [this other])
             (mul [this other]))
             

(defrecord vec3 [x y z] 
           vec-ops
           (v [this]
              (vector x y z))
           (normalize [this] 
                      ;{:post [(instance? vec3 %)]}
                      (let [l (/ 1.0 (length this))
                            nv (vec (map (partial * l) (v this)))]
                        (vec3. (nv 0) (nv 1) (nv 2))))
           (length [this] 
                   ;{:post [(number? %)]}
                   (let [xyz (v this)
                         len-squared (reduce + (map * xyz xyz))]
                     (m/sqrt len-squared)))
           (dot [this other]
                ;{:pre [(instance? vec3 other)]
                ; :post [(number? %)]}
                (reduce + (map * (v this) (v other))))
           (cross [this other]
                  ;{:pre [(instance? vec3 other)]
                  ; :post [(instance? vec3 %)]}
                  (let [[x1 y1 z1] (v this)
                        [x2 y2 z2] (v other)
                        nx (* y1 (- z2 z1) y2)
                        ny (* z1 (- x2 x1) z2)
                        nz (* x1 (- y2 y1) x2)]
                    (vec3. nx ny nz)))
           (add [this other]
                ;{:pre [(instance? vec3 other)]
                ; :post [(instance? vec3 %)]}
                (let [nv (vec (map + (v this) (v other)))]
                  (vec3. (nv 0) (nv 1) (nv 2))))
           (minus [this other]
                  ;{:pre [(instance? vec3 other)]
                  ; :post [(instance? vec3 %)]}
                  (let [nv (vec (map - (v this) (v other)))]
                    (vec3. (nv 0) (nv 1) (nv 2))))
           (mul [this other]
                ;{:pre [(or (instance? vec3 other) (number? other))]
                ; :post [(instance? vec3 %)]}
                (cond 
                  (= (type other) vec3)
                  (let [nv (vec (map * (v this) (v other)))]
                    (vec3. (nv 0) (nv 1) (nv 2)))
                  (number? other)
                  (let [nv (vec (map (partial * other) (v this)))]
                    (vec3. (nv 0) (nv 1) (nv 2)))
                  :else 
                  (throw (Exception. 
                           (str "other arg is of type " (type other)))))))









