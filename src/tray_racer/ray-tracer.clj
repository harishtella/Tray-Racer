(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:use clojure.stacktrace)
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing] [rosado.processing.applet]))

(def p p/pprint)

;(def rrr (Ray. [-5.5 -0.5 0] [0 0 1]))
;(def bbb (get-prim-hit-at rrr))   
;(calc-color bbb)

(s/init-scene)

(def o [0 0 -5])
(def real-d [200 150])
(def proj-d [[-4 4] [3 -3]])

;; dir must not be [0 0 0]  
(defrecord Ray [orig dir])

(defn ray-point [ray t]
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))

(defn map-to-range [dist [r1 r2]]
  (let [r (- r2 r1)
        dist-r (* dist r)
        corr (+ dist-r r1)]
    corr))

(defn real->proj [coor]
  (let [rats (map / coor real-d)
        proj (vec (map map-to-range rats proj-d))]
    proj))

(defn get-prim-hit-at [ray] 
  (if-let [prim-hit (->>
                   @s/Scene
                   (map #(s/intersect % ray))
                   (filter #(number? (second %)))
                   (sort-by second <)
                   (first))]
    [(first prim-hit) (ray-point ray (second prim-hit))]
    nil))


(defn calc-color [[prim hit-point]]
  (if (:is-light prim) 
    [1 1 1]
    (let [n (s/get-normal prim hit-point)]
      (loop [color [0 0 0] lights (s/get-lights)]
        (if-let [light (first lights)]
          (let [l (v/- (:center light) hit-point)
                l (v/norm l)
                dot (v/dot n l)
                diff (* dot (s/m :diffuse prim))
                c (reduce v/* 
                          [diff (s/m :color light) (s/m :color prim)])]
            (recur (v/+ color c) (next lights)))
          color)))))


(defn iterate-with-end [f x] 
  (let [next-x (f x)]
    (if (= next-x nil)
      (list x)
      (cons x (lazy-seq (iterate-with-end f next-x))))))

(def real-d [2 2])

(def vn 
  (let [[rx ry] (map dec real-d)]
    (letfn [(gen-f [[x y]]
                   (cond
                     (and (>= x rx) (>= y ry))
                     nil
                     (>= y ry) 
                     [(inc x) 0]
                     :else 
                     [x (inc y)]))]
      (iterate-with-end gen-f [0 0]))))

(defn fire []
  (when-let [[x y] (get-vn)]
    (let [proj-coor (real->proj [x y])
          dir (vec (concat proj-coor [0]))
          ray (Ray. o dir)]
      (if-let [hit (get-prim-hit-at ray)]
        (let [c (calc-color hit)]
          (set-pixel x y (apply color c)))
        (set-pixel x y (color 0 0 0))))))

(defn fire-2 []
  (for [x (range (first real-d)) y (range (second real-d))]
    (let [proj-coor (real->proj [x y])
          dir (vec (concat proj-coor [0]))
          ray (Ray. o dir)]
      (if-let [hit (get-prim-hit-at ray)]
        (let [c (calc-color hit)]
          (set-pixel x y (apply color c)))
        (set-pixel x y (color 0 0 0))))))


(defn draw []
  (fire))


(defn setup []
  "Runs once."
  (smooth)
  (background-float 225)
  (stroke-float 10)
  (framerate 9999))

(defapplet tr :title "tray-racer: best graphics"
  :setup setup :draw draw :size real-d)
  
(run tr)

(stop tr)
