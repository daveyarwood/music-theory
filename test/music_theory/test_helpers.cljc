(ns music-theory.test-helpers)

(defn =ish
  "Returns true if all arguments are within 0.01 of each other."
  [& xs]
  (let [[x & xs] (sort xs)]
    (apply <= x (conj (vec xs) (+ x 0.01)))))
