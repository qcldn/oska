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

(defn adjacent-cells [[x y]]
  #{[(dec x) (dec y)]
    [(inc x) (dec y)]
    [(dec x) (inc y)]
    [(inc x) (inc y)]})

(defn occupied-cells [pieces]
  (set (map #(vector (:x %) (:y %)) pieces)))

(def cells
  #{[0 0]       [4 0]
      [1 1]   [3 1]
    [0 2] [2 2] [4 2]
      [1 3]   [3 3]
    [0 4] [2 4] [4 4]
      [1 5]   [3 5]
    [0 6]       [4 6]})

(defn- ->cell [piece]
  [(:x piece) (:y piece)])

(defn valid-move? [pieces piece-to-move cell]
  (and (contains? cells cell)
       (contains? (adjacent-cells (->cell piece-to-move)) cell)
       (not (contains? (occupied-cells pieces) cell))))


;  0  1  2  3  4
; 0x           x
; 1   x     x
; 2x     x     x
; 3   x     x
; 4x     x     x
; 5   x     x
; 6x           x


