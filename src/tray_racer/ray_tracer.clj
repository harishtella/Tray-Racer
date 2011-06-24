(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:use tray-racer.utils)
  (:use clojure.stacktrace)
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing]))

(s/init-scene)

; origin of rays to be traced in world coordinates
(def o [0 0 -5])

; dimensions of program window in screen pixels
(def window-dim [200 150])

; projection plane location world coordinates 
(def proj-plane-location [[-4 4] [3 -3] 3])

;; dir must not be [0 0 0]  
(defrecord Ray [orig dir])

;; returns a point along the ray
(defn ray-point [ray t]
  {:pre [(and (>= t 0) (<= t 1))]}
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))

;; Given coor, a point in screen pixels on program window,
;; returns a point in world coordinates on the projection
;; plane.
(defn real-coord->proj-coord [coord]
  (let [ratios (map / coord window-dim)
        px (map-to-range (nth ratios 0) (nth proj-plane-location 0))
        py (map-to-range (nth ratios 1) (nth proj-plane-location 1))
        pz (nth proj-plane-location 2)]
    (vector px py pz)))
      

; Returns the first primitive in the scene hit by  
; ray or returns nil if no primitives are hit
(defn get-prim-hit-at [ray] 
  (if-let [prim-hit (->>
                   @s/Scene
                   (map #(s/intersect % ray))
                   (filter #(number? (second %)))
                   (sort-by second <)
                   (first))]
    [(first prim-hit) (ray-point ray (second prim-hit))]
    nil))

(defn c1->c255 [x]
  (map-to-range x [0 255]))

(defn calc-color [[prim hit-point]]
  (if (:is-light prim) 
    [1 1 1]
    (let [n (s/get-normal prim hit-point)]
      (loop [color [0 0 0] lights (s/get-lights)]
        (if-let [light (first lights)]
          (let [l (v/- (:center light) hit-point)
                l (v/norm l)
                dot (v/dot n l)
                diff (* dot (s/mat-prop :diffuse prim))]
            (if (> diff 0)
              (let [c (reduce v/* 
                              [diff (s/mat-prop :color light) (s/mat-prop :color prim)])]
                (recur (v/+ color c) (next lights)))
              (recur color (next lights))))
          color)))))


; A list of all the screen window coordinates. 
; It is used for calculating color values. 
;
(def window-coords
  (let [[rx ry] (map dec window-dim)]
    (letfn [(gen-next-coord [[x y]]
                           (cond
                             (and (>= x rx) (>= y ry)) nil
                             (>= y ry)  [(inc x) 0]
                             :else [x (inc y)]) )]
      (iterate-until-end gen-next-coord [0 0]))))

; Takes in a window coordinate, translates it to a point on
; the projection plane, traces a ray that goes through that
; point computing the resulting color, then finally it sets
; the window coordinate to this color. 
;
(defn fire-ray-from [[x y]]
    (let [proj-coord (real-coord->proj-coord [x y])
          ray (Ray. o proj-coord)]
      (if-let [hit (get-prim-hit-at ray)]
        (let [c (map c1->c255 (calc-color hit))]
          (set-pixel x y (apply color c)))
        (set-pixel x y (color 0 0 0)))))

; Fires the next ray to be traced.
;
(def fire 
  (let [delayed-firings (atom (map fire-ray-from window-coords))]
    (fn [] (do-one delayed-firings))))

