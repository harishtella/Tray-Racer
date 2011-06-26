;; ray_tracer.clj 
;; ----------------------------------
;; the heart of tray-racer


(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:use tray-racer.utils)
  (:use clojure.stacktrace)
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing]))


;; COORDINATES
;; needed for ray-tracing 
;; ---------------------------------------------

;; origin of the rays to be traced (in world coordinates)
(def o [0 0 -5])

;; projection plane location (in world coordinates)
(def proj-plane-location [[-4 4] [3 -3] 3])

;; dimensions of program window in pixels
(def window-dim [200 150])

;; a list of all the window coordinates. 
(def window-coords
  (let [[rx ry] (map dec window-dim)]
    (letfn [(gen-next-coord [[x y]]
                           (cond
                             (and (>= x rx) (>= y ry)) nil
                             (>= y ry)  [(inc x) 0]
                             :else [x (inc y)]) )]
      (iterate-until-end gen-next-coord [0 0]))))


;; RAYS
;; ---------------------------------------------

(defrecord Ray [orig dir])

(defn ray-point [ray t]
  "returns a point along the ray"
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))


;; RAY-TRACING
;; ---------------------------------------------

(defn real-coord->proj-coord [coord]
  "Given coord, a point in window coordinates, this function
  returns a point in world coordinates on the projection
  plane."

  (let [ratios (map / coord window-dim)
        px (map-to-range (nth ratios 0) (nth proj-plane-location 0))
        py (map-to-range (nth ratios 1) (nth proj-plane-location 1))
        pz (nth proj-plane-location 2)]
    (vector px py pz)))
      

(defn first-prim-hit-by [ray] 
  "Returns a map containing: 
  :prim-hit - the first primitive hit by ray
  :t - the distance along the ray at which it hits the
       first primitve 
  :hit-point - the point at which the ray hits the
               first primitive

  OR 
  if the ray does not hit any primitives, nil is returned "

  (if-let [first-prim-hit (->>
                            @s/Scene
                            (map #(s/intersect % ray))
                            (filter #(number? (:t %)))
                            (sort-by :t <)
                            (first))]
    (assoc first-prim-hit :hit-point (ray-point ray (:t first-prim-hit)))
    nil))

(defn c1->c255 [x]
  (map-to-range x [0 255]))

(def full-bright-color [1 1 1])
(def no-color [0 0 0])

(defn color-from-light [{:keys [prim-hit hit-point n]} light]
  "Returns the color contributed by a single light at hit-point."
  
  (let [l (v/norm (v/- (:center light) hit-point))
        light-incidence (v/dot n l)
        diffuse-component (* light-incidence (s/mat-prop :diffuse prim-hit))]
    (if (> diffuse-component 0) 
      (reduce v/* [diffuse-component 
                   (s/mat-prop :color light) 
                   (s/mat-prop :color prim-hit)])
      no-color)))


(defn calc-color [{:keys [prim-hit hit-point] :as hit-info}]
  "Returns the color at hit-point."

  (if (:is-light prim-hit) 
    (map c1->c255 full-bright-color)
    (let [n (s/get-normal prim-hit hit-point)
          hit-info-2 (assoc hit-info :n n)
          lights (s/get-lights)
          ;; TODO use binding to simplify this
          color-from-light (partial color-from-light hit-info-2)
          color (reduce v/+ (map color-from-light lights))]
      (map c1->c255 color))))



(defn fire-ray-from [[x y]]
  "Takes in a window coordinate, translates it to a point on
  the projection plane, and traces a ray that goes through
  this point. If the ray hits a primitive, the original
  window coordinate is set to the appropriate color value.
  Otherwise the original window coordinate is set to be
  black. "

  (let [proj-coord (real-coord->proj-coord [x y])
        ray (Ray. o proj-coord)]
    (if-let [hit-info (first-prim-hit-by ray)]
      (let [color-val (calc-color hit-info)]
        (set-pixel x y (apply color color-val)))
      (set-pixel x y (apply color no-color)))))

;;  A function that executes the next element of
;;  delayed-firings. This in turn will fire a ray and the
;;  resulting color value will be drawn to the screen.
;;
;;  When delayed-firings is empty, a color will have been
;;  computed for every pixel in the program's window. "
;;
(def fire 
  (let [delayed-firings (atom (map fire-ray-from window-coords))]
    (fn [] (do-one delayed-firings))))

