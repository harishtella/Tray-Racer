(ns tray-racer.scene
  (:require [clojure.contrib.math :as m])
  (:require [tray-racer.vec3 :as v])
  (:require [clojure.pprint :as p])
  (:use alex-and-georges.debug-repl))


;; MATERIALS
;; ---------------------------------------------
;;
;;
;;

;; vec3 float float
(defrecord Material [color reflection diffuse])

(defn specular-of [mat]
  (- 1.0 (:diffuse mat)))




;; PRIMITIVES 
;; ---------------------------------------------
;;
;;
;;

(defprotocol primitive 
             (intersect [this ray])
             (get-normal [this pos]))

(defrecord Sphere [center rad name is-light material]
           primitive
           (intersect [this ray]
                      (let [o-c (v/- (:orig ray) center)
                            d (:dir ray)
                            A (v/dot d d)
                            B (* 2 (v/dot o-c d))
                            ;; XXX is v/dot commutative with vec/* ?
                            C (- (v/dot o-c o-c)
                                 (* rad rad))
                            det (- (m/expt B 2)
                                   (* 4 A C))]
                        (if (zero? A)
                          (p/pprint d))
                        (if (< det 0) 
                          [this 'miss]
                          (let [sqrt-det (m/sqrt det)
                                q (/ 
                                    ((if (< B 0) + -) 
                                        (- B) 
                                        sqrt-det) 
                                     2)
                                t0-t (/ q A)
                                t1-t (/ C q)
                                t0 (min t0-t t1-t)
                                t1 (max t0-t t1-t)]
                            (cond
                              ;; sphere in rays reverse path
                              (< t1 0)
                              [this 'miss]
                              ;; hit from inside sphere
                              (< t0 0)
                              [this 'inside-hit]
                              ;; OK hit
                              :else 
                              [this t0])))))
           (get-normal [this pos]
                       (v/* (v/- pos center)
                                (/ 1 (* rad rad)))))

;; vec3 float 
(defrecord Plane [normal d name is-light material]
           primitive
           ;; XXX not sure how this works
           (intersect [this ray]
                      (let [den (v/dot normal (:dir ray))]
                        (if (not= 0 den)
                          (let [dist (/
                                       (- (+ d 
                                             (v/dot normal (:orig ray))))
                                       den)]
                            (if (<= dist 0)  
                              [this 'miss]
                              [this dist]))
                          [this 'miss])))
           (get-normal [this pos]
                       normal))

;; vec3 vec3 
(defrecord Plane-with-point [normal p name is-light material]
           primitive
           (intersect [this ray]
                      (let [den (v/dot normal (:dir ray))
                            dist (/
                                   (v/dot (v/- p (:orig ray)) normal)
                                   den)]
                        (cond
                          (= den 0) [this 'miss]
                          (<= dist 0) [this 'miss]
                          :else [this dist])))
           (get-normal [this pos]
                       normal))



;; returns the value of prop from material record within prim
(defn mat-prop [prop prim]
  (prop (:material prim)))




;; SCENE 
;; ---------------------------------------------
;;
;;
;;

;; holds all the elements of the scene 
(def Scene (atom []))

(defn add-to-scene [x]
  (swap! Scene conj x)) 

(defn get-lights []
  (filter #(:is-light %) @Scene))

(defn init-scene []
  (let [init-prims 
        (list (Plane. [0 1 0] 4.4 "plane" false 
                      (Material. [0.4 0.3 0.3] 0 1.0))
              (Sphere. [1 -0.8 3] 2.5 "big sphere" false 
                       (Material. [0.7 0.7 0.7] 0.6 0.4))
              (Sphere. [-5.5 -0.5 7] 2 "small sphere" false
                       (Material. [0.7 0.7 1.0] 1.0 0.4))
              (Sphere. [0 5 5] 0.1 "" true 
                       (Material. [0.6 0.6 0.6] 0 0))
              (Sphere. [2 5 1] 0.1 "" true
                       (Material. [0.7 0.7 0.9] 0 0)))]
    (reset! Scene (vec init-prims))))

