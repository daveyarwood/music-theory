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

(defn well->
  "A higher-order function that creates a tuning function for a well-tempered
   tuning system, given a list of 12 ratios."
  [ratios]
  (fn [ref-pitch midi-note tonic]
    (assert tonic
      "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
    (let [octave    (note/octave midi-note)
          base-note (:number (note/->note (str (name tonic) octave)))
          base-hz   (equal-> ref-pitch base-note)
          below?    (< midi-note base-note)
          n         (note/note-position tonic midi-note)
          ratio     (nth ratios n)
          freq      (* base-hz ratio)]
      (if below? (/ freq 2.0) freq))))

(defn <-well
  "A higher-order function that creates a reverse tuning function for a
   well-tempered tuning system, given a list of 12 ratios."
  [ratios]
  (fn [ref-pitch freq tonic]
    (assert tonic
      "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
    ; TODO
    #_(let [octave    (note/octave midi-note)
          base-note (:number (note/->note (str (name tonic) octave)))
          base-hz   (equal-> ref-pitch base-note)
          below?    (< midi-note base-note)
          n         (note/note-position tonic midi-note)
          ratio     (nth ratios n)
          freq      (* base-hz ratio)]
      (if below? (/ freq 2.0) freq))))

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
  (let [f (well-> werckmeister-iii-ratios)]
    (f ref-pitch midi-note tonic)))

(defn <-werckmeister-iii
  [ref-pitch frequency tonic]
  (assert tonic
    "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
  "TODO")

(def just-ratios
  [1
   (/ 16 15)
   (/ 9 8)
   (/ 6 5)
   (/ 5 4)
   (/ 4 3)
   (/ 45 32)
   (/ 3 2)
   (/ 8 5)
   (/ 5 3)
   (/ 9 5)
   (/ 15 8)])

(defn just->
  "Just intonation is any musical tuning in which the frequencies of the notes
   are related by ratios of small whole numbers.
   (source: https://en.wikipedia.org/wiki/Just_intonation)

   There are a lot of variations on these ratios. These ones were picked
   somewhat arbitrarily as a good general example of just intonation.
   (ref: http://www.sfu.ca/sonic-studio/handbook/Just_Tuning.html)"
  [ref-pitch midi-note tonic]
  (let [f (well-> just-ratios)]
    (f ref-pitch midi-note tonic)))

(defn <-just
  [ref-pitch frequency tonic]
  (assert tonic
    "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
  "TODO")

(def young-ratios
  [1
   (/ 567 512)
   (/ 9 8)
   (/ 147 128)
   (/ 21 16)
   (/ 1323 1024)
   (/ 189 128)
   (/ 3 2)
   (/ 49 32)
   (/ 7 4)
   (/ 441 256)
   (/ 63 32)])

(defn young->
  "The piano tuning used by La Monte Young in his composition The Well-Tuned
   Piano. It is a form of just intonation based on a modified seven-limit
   tuning process.
   (source: https://en.wikipedia.org/wiki/The_Well-Tuned_Piano#Tuning)

   Interestingly, this scale does not uniformly ascend. A couple notes are
   actually lower in pitch than the next note up. This is so that all perfect
   fifths (3/2 ratios) will be spelled as perfect fifths on the keyboard.
   (source: http://www.kylegann.com/wtp.html)"
  [ref-pitch midi-note tonic]
  (let [f (well-> young-ratios)]
    (f ref-pitch midi-note tonic)))

(defn <-young
  [ref-pitch frequency tonic]
  (assert tonic
    "Well-tempered tunings are based on a tonic note; *tonic* cannot be nil.")
  "TODO")
