(ns oska.core)

(enable-console-print!)

(defn- winning-row [player]
  (if (= player :red) 0 4))

(defn- player-pieces [player pieces]
  (filter #(= player (:player %)) pieces))

(defn won? [player pieces]
  (->> pieces
       (player-pieces player)
       (every? #(= (:x %) (winning-row player)))))

(defn- ->cell [piece]
  [(:x piece) (:y piece)])

(defn occupied-cells [pieces]
  (set (map #(->cell %) pieces)))

(def initial-game
  [{:player :blue :x 0 :y 0}
   {:player :blue :x 0 :y 2}
   {:player :blue :x 0 :y 4}
   {:player :blue :x 0 :y 6}
   {:player :red :x 4 :y 0}
   {:player :red :x 4 :y 2}
   {:player :red :x 4 :y 4}
   {:player :red :x 4 :y 6}])

(def cells
  #{[0 0]       [4 0]
      [1 1]   [3 1]
    [0 2] [2 2] [4 2]
      [1 3]   [3 3]
    [0 4] [2 4] [4 4]
      [1 5]   [3 5]
    [0 6]       [4 6]})

(def directions
  [[1 1] [-1 -1] [1 -1] [-1 1]])

(defn- cell-add [& cells]
  (reduce #(map + %1 %2) [0 0] cells))

(defn adjacent-cells [cell]
  (set (map #(cell-add cell %) directions)))

(defn- enemy-player [player]
  (if :red :blue :red))

(defn- enemy-jump-cells [pieces player cell]
  (let [enemy-cells (map ->cell (player-pieces (enemy-player player) pieces))]
    (->> directions
         (filter #(some #{(cell-add cell %1)} enemy-cells))
         (map #(cell-add cell % %))
         (set))))

(defn valid-move? [pieces piece-to-move cell]
  (and (contains? cells cell)
       (not (contains? (occupied-cells pieces) cell))
       (or (contains? (adjacent-cells (->cell piece-to-move)) cell)
           (contains? (enemy-jump-cells pieces (:player piece-to-move) (->cell piece-to-move)) cell))))


;  0  1  2  3  4
; 0x           x
; 1   x     x
; 2x     x     x
; 3   x     x
; 4x     x     x
; 5   x     x
; 6x           x

(defn draw-cells [cells svg-element]
  (doall (map #(let [[x y] %
        circle-element (.createElementNS js/document "http://www.w3.org/2000/svg" "circle")]
    (.add (.-classList circle-element) "cell")
    (.setAttribute circle-element "cx" (+ 25 (* 50 x)))
    (.setAttribute circle-element "cy" (+ 25 (* 50 y)))
    (.setAttribute circle-element "r" 2)
    (.appendChild svg-element circle-element)) cells)))

(defn draw-rect-cells [cells svg-element]
  (doall (map #(let [[x y] %
        rect-element (.createElementNS js/document "http://www.w3.org/2000/svg" "rect")]
        (.setAttribute rect-element "x" (+ 25 (* 50 x)))
        (.setAttribute rect-element "y" (+ 25 (* 50 y)))
        (.setAttribute rect-element "height" 71)
        (.setAttribute rect-element "width" 71)
        (.setAttribute rect-element "style" "transform-origin: center; transform: translate(-35px, -35px) rotate(45deg); fill: white; stroke: black; stroke-width: 2px;")
        (.appendChild svg-element rect-element)) cells)))

(defn draw-pieces [pieces svg-element]
  (doall (map #(let [piece %
                circle-element (.createElementNS js/document "http://www.w3.org/2000/svg" "circle")]
    (.add (.-classList circle-element) "piece")
    (.setAttribute circle-element "cx" (+ 25 (* 50 (:x piece))))
    (.setAttribute circle-element "cy" (+ 25 (* 50 (:y piece))))
    (.setAttribute circle-element "r" 10)
    (.setAttribute circle-element "fill" (name (:player piece)))
    (.appendChild svg-element circle-element)) pieces)))

(when-let [svg-element (.querySelector js/document "svg")]
  (draw-rect-cells cells svg-element)
  (draw-cells cells svg-element)
  (draw-pieces initial-game svg-element))
