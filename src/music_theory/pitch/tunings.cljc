(ns music-theory.pitch.tunings
  (:require [music-theory.note :as note]))

(comment
  "The tuning functions in this namespace are named with either a <- prefixed
   or -> suffixed. The -> suffix refers to converting a MIDI note to a
   frequency in Hz, and the <- prefix refers to converting a frequency in Hz to
   the nearest MIDI note based on that tuning.")

(defn equal->
  "Equal temperament: so convenient, yet so ugly."
  [ref-pitch midi-note]
  (* ref-pitch (Math/pow 2 (/ (- midi-note 69) 12.0))))

(defn <-equal
  [ref-pitch freq]
  (letfn [(log2 [n] (/ (Math/log n) (Math/log 2)))]
    (Math/round (+ 69 (* 12 (log2 (/ freq ref-pitch)))))))

(def werckmeister-iii-ratios
  [1
   (/ 256 243)
   (* (/ 64 81) (Math/sqrt 2))
   (/ 32 27)
   (* (/ 256 243) (Math/pow 2 0.25))
   (/ 4 3)
   (/ 1024 729)
   (* (/ 8 9) (Math/pow 8 0.25))
   (/ 128 81)
   (* (/ 1024 729) (Math/pow 2 0.25))
   (/ 16 9)
   (* (/ 128 81) (Math/pow 2 0.25))])

(defn werckmeister-iii->
  "Werckmeister I (III): 'correct temperament' based on 1/4 comma divisions
   (source: https://en.wikipedia.org/wiki/Werckmeister_temperament)

   Uses equal temperament as the standard for finding the starting pitch, based
   on the reference pitch and the tonic, then uses the Werckmeister III ratios
   to tune based on the tonic."
  [ref-pitch midi-note tonic]
  (assert tonic
    "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
  (let [base-note (note/->note (str (name tonic) 1))
        base-hz   (equal-> ref-pitch base-note)]))

(defn <-werckmeister-iii
  [ref-pitch frequency tonic]
  (assert tonic
    "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
  "TODO")
