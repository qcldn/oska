(ns oska.core-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test :as test]
            [oska.core :as core]))

;[1 2 3]
;{:a 1 :b 2}
; #{1 2 3}
(defn- ->piece [player x y]
  {:player player :x x :y y})

(deftest won? []
  (testing "win for red"
    (is (core/won? :red [(->piece :red 0 0)])))
  (testing "not win for red"
    (is (not (core/won? :red [(->piece :red 1 1)]))))
  (testing "another win for red"
    (is (core/won? :red [(->piece :red 0 2)])))
  (testing "win for blue"
    (is (core/won? :blue [(->piece :blue 4 0)])))
  (testing "another not win for red"
    (is (not (core/won? :red [(->piece :red 0 2) (->piece :red 1 1)]))))
  (testing "win for red with blue still in play"
    (is (core/won? :red [(->piece :red 0 0) (->piece :blue 1 1)]))))

(deftest valid-move? []
  (testing "red can move to empty adjacent cell"
    (is (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [0 0])))
  (testing "red cannot move to non-adjacent cell"
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [0 4])))
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [1 3])))
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [3 1]))))
  (testing "cannot move to occupied cell"
    (is (not (core/valid-move? [(->piece :red 1 1) (->piece :blue 0 0)] (->piece :red 1 1) [0 0]))))
  (testing "cannot move to non-existent cell"
    (is (not (core/valid-move? [(->piece :red 1 1) (->piece :blue 0 0)] (->piece :red 1 1) [2 0]))))
  (testing "can jump"
    (is (core/valid-move? [(->piece :red 2 2) (->piece :blue 1 1)] (->piece :red 2 2) [0 0])))
  (testing "doesn't jump unless the other player is adjacent"
    (is (not (core/valid-move? [(->piece :red 2 2)] (->piece :red 2 2) [0 0])))))

(defn- getIntAttribute [element k]
  (js/parseInt (.getAttribute element k)))

(defn- ->pos [circle-element]
 [(getIntAttribute circle-element "cx") (getIntAttribute circle-element "cy")])

(defn- ->fill [circle-element] 
  (.getAttribute circle-element "fill"))

(defn- perform-draw-cells [cells]
  (let [svg-element (.createElement js/document "svg")]
    (core/draw-cells cells svg-element)
    (array-seq (.querySelectorAll svg-element "svg circle.cell"))))

(defn- perform-draw-pieces [pieces]
  (let [svg-element (.createElement js/document "svg")]
    (core/draw-pieces pieces svg-element)
    (array-seq (.querySelectorAll svg-element "svg circle.piece"))))

(deftest draw-cells []
  (testing "draws a single cell"
     (is (= 1 (count (perform-draw-cells #{[0 0]})))))
  (testing "draws cell 0 0 in 25 25"
     (is (= [25 25] (->pos (first (perform-draw-cells #{[0 0]}))))))
  (testing "draws cell 1 1 in 75 75"
     (is (= [75 75] (->pos (first (perform-draw-cells #{[1 1]}))))))
  (testing "draws multiple cells"
     (is (= [[25 25] [75 75]] (map ->pos (perform-draw-cells #{[0 0] [1 1]})))))
  (testing "circles have r 2"
     (is (= 2 (getIntAttribute (first (perform-draw-cells #{[0 0]})) "r")))))

(deftest draw-pieces []
  (testing "draws a piece"
    (is (= 1 (count (perform-draw-pieces [(->piece :red 0 0)])))))
  (testing "piece has red fill"
    (is (= "red" (->fill (first (perform-draw-pieces [(->piece :red 0 0)]))))))
  (testing "piece has blue fill"
    (is (= "blue" (->fill (first (perform-draw-pieces [(->piece :blue 0 0)]))))))
  (testing "draws piece 0 0 in 25 25"
    (is (= [25 25] (->pos (first (perform-draw-pieces [(->piece :blue 0 0)]))))))
  (testing "draws piece 1 1 in 75 75"
    (is (= [75 75] (->pos (first (perform-draw-pieces [(->piece :blue 1 1)]))))))
  (testing "circles have radius 10"
    (is (= 10 (getIntAttribute (first (perform-draw-pieces [(->piece :blue 0 0)])) "r")))) 
  (testing "draws multiple pieces"
    (is (= [[25 25] [75 75]] (map ->pos (perform-draw-pieces [(->piece :blue 0 0) (->piece :blue 1 1)]))))))
