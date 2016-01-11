(ns music-theory.test-helpers)

(defn round
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(defn =ish
  "Returns true if all arguments are within 0.01 of each other."
  [& xs]
  (let [[x & xs] (sort xs)]
    (apply <= x (conj (vec xs) (+ x 0.01)))))
