(ns tray-racer.scene)

(defrecord Ray [orig dir])

;; vec3 float float
(defrecord Material [color reflection diffuse])
(defn specular-of [mat]
  (- 1.0 (:diffuse mat)))

(defprotocol primitive 
             (intersect [ray dist])
             (get-normal [pos]))

(defrecord Sphere [center radius name is-light material]
           primitive
           (intersect [ray dist]
                      (let [r2 (* radius radius)
                            o (v/- (:orig ray) center)
                            b (* -1
                                 (v/dot o (:dir ray)))
                            det (+ (* b b)
                                   (* -1 (v/dot o o))
                                   r2)]
                        (if (<= det 0)
                          'miss
                          (let [det (m/sqrt det)
                                i1 (- b det)
                                i2 (+ b det)]
                            (if (and (> i2 0)
                                     (< i1 0)




                            

                                   


           (get-normal [pos]
                       (v/* (v/- pos center)
                                (* radius radius)))

;; vec3 float
(defrecord Plane [normal d name is-light material]
           primitive)

(defrecord Scene [primitives])

