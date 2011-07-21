(ns tray-racer.test.scene
  (:use [clojure.test])
  (:use [tray-racer.scene] :reload)
  (:import tray-racer.scene.Plane)
  (:import tray-racer.scene.Sphere)
  (:import tray-racer.scene.Material)
  (:import tray-racer.ray-tracer.Ray))
  
(defn isnt [x] (is (not x)))

(def test-plane  (Plane. [0 1 0] 4.4 "plane" false
                         (Material. [0.4 0.3 0.3] 0 1.0)))
(def test-sphere (Sphere. [-5.5 -0.5 7] 2 "small sphere" false 
                          (Material. [0.7 0.7 1.0] 1.0 0.4)))
(def test-ray (Ray. [-1 -1 -1] [1 2 3]))

(deftest plane-normal
         (is (= (get-normal test-plane [1 2 3]) [0 1 0])))

(deftest plane-intersection
         (is (= (:t (intersect test-plane test-ray)) 'miss)))

(deftest sphere-normal 
         (is (= (get-normal test-sphere [1 2 3]) [1.625 0.625 -1])))

(deftest sphere-intersection
         (is (= (:t (intersect test-sphere test-ray)) 'miss)))



