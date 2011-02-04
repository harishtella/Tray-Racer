(ns tray-racer.test.vec3
  (:use [tray-racer.vec3] :reload)
  (:refer-clojure :rename {+ cc+ - cc- * cc*})
  (:use [clojure.test]))

(deftest stupid-test 
         (testing "some stupid bullshit")
         (let [x [2 3 5]]
           (println x)
           (println (norm x))
           (println (len x))
           (println (dot x [1 2 3]))
           (println (cross x [1 2 3]))
           (println (+ x [1 2 3]))
           (println (- x [1 2 3]))
           (println (* x [1 2 3]))
           (println (* 7 x))
           (println (* x 3.5)))

           ;dummy test
           (is (= 3 3)))

