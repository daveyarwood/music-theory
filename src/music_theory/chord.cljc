(ns music-theory.chord
  (:require [music-theory.note :refer (interval+)]))

(defn build-chord
  "Given a base (lowest) note and either:

     - a sequence of intervals like :m3, :P5, etc.
     - the name of a chord, like :Cmaj7 (TODO: more info)

   Returns a list of the notes in the chord, in order from lowest to highest."
  [base-note x]
  (if (coll? x)
    (reductions interval+ base-note x)
    "TODO: build chord based on base note and chord name"))

