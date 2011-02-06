(ns tray-racer.scene)

(defrecord Ray [orig dir])

;; vec3 float float
(defrecord Material [color reflection diffuse])
(defn specular-of [mat]
  (- 1.0 (:diffuse mat)))

(defprotocol primitive 
             (intersect [ray dist])
             (get-normal [pos]))

(defrecord Sphere [center rad name is-light material]
           primitive
           (intersect [ray dist]
                      (let [o-c (v/- (:orig ray) center)
                            d (:dir ray)
                            A (v/dot d d)
                            B (* 2 (v/dot o-c d))
                            ;; XXX is v/dot commutative with vec/* ?
                            C (- (v/dot o-c o-c)
                                 (* rad rad))
                            ;; XXX does this work
                            det (- (** B 2)
                                   (* 4 A C))]
                        (if (< det 0) 
                          'miss
                          (let [sqrt-det (m/sqrt det)
                                q (/ ((if (< B 0) '+ '-) 
                                        ;; XXX can I do this
                                        (- B) 
                                        ;; XXX can I do this
                                        sqrt-det) 
                                     2)




                        
                                   


           (get-normal [pos]
                       (v/* (v/- pos center)
                                (* radius radius)))

;; vec3 float
(defrecord Plane [normal d name is-light material]
           primitive)

(defrecord Scene [primitives])

