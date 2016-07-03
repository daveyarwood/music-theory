(ns music-theory.chord
  (:require [music-theory.note :refer (interval+)]
            [music-theory.util :refer (error)]))

(def ^:private chord-intervals
  {""         [:M3 :m3]                 ; major
   "M"        [:M3 :m3]
   "maj"      [:M3 :m3]
   "m"        [:m3 :M3]                 ; minor
   "min"      [:m3 :M3]
   "dim"      [:m3 :m3]                 ; diminished
   "°"        [:m3 :m3]
   "aug"      [:M3 :M3]                 ; augmented
   "+"        [:M3 :M3]
   "+5"       [:M3 :M3]
   "5"        [:P5]                     ; fifth
   "6"        [:M3 :m3 :P2]             ; major sixth
   "M6"       [:M3 :m3 :P2]
   "maj6"     [:M3 :m3 :P2]
   "m6"       [:m3 :M3 :P2]             ; minor sixth
   "min6"     [:m3 :M3 :P2]
   "M7"       [:M3 :m3 :M3]             ; major seventh
   "maj7"     [:M3 :m3 :M3]
   "7"        [:M3 :m3 :m3]             ; dominant seventh
   "dom7"     [:M3 :m3 :m3]
   "m7"       [:m3 :M3 :m3]             ; minor seventh
   "min7"     [:m3 :M3 :m3]
   "ø"        [:m3 :m3 :M3]             ; half diminished seventh
   "ø7"       [:m3 :m3 :M3]
   "m7b5"     [:m3 :m3 :M3]
   "°7"       [:m3 :m3 :m3]             ; fully diminished seventh
   "dim7"     [:m3 :m3 :m3]
   "m+7"      [:m3 :M3 :M3]             ; minor-major seventh
   "min+7"    [:m3 :M3 :M3]
   "aug7"     [:M3 :M3 :d3]             ; augmented-minor seventh
   "+7"       [:M3 :M3 :d3]
   "7+5"      [:M3 :M3 :d3]
   "7#5"      [:M3 :M3 :d3]
   "7b5"      [:M3 :d3 :M3]             ; major-minor w/ lowered fifth
   "M9"       [:M3 :m3 :M3 :m3]         ; major ninth
   "maj9"     [:M3 :m3 :M3 :m3]
   "9"        [:M3 :m3 :m3 :M3]         ; dominant ninth
   "dom9"     [:M3 :m3 :m3 :M3]
   "mM9"      [:m3 :M3 :M3 :m3]         ; minor-major ninth
   "minmaj9"  [:m3 :M3 :M3 :m3]
   "m9"       [:m3 :M3 :m3 :M3]         ; minor ninth
   "min9"     [:m3 :M3 :m3 :M3]
   "+M9"      [:M3 :M3 :m3 :m3]         ; augmented major ninth
   "augmaj9"  [:M3 :M3 :m3 :m3]
   "+9"       [:M3 :M3 :d3 :M3]         ; augmented dominant ninth
   "aug9"     [:M3 :M3 :d3 :M3]
   "9#5"      [:M3 :M3 :d3 :M3]
   "ø9"       [:m3 :m3 :M3 :M3]         ; half diminished ninth
   "øb9"      [:m3 :m3 :M3 :m3]         ; half diminished minor ninth
   "°9"       [:m3 :m3 :m3 :A3]         ; diminished ninth
   "dim9"     [:m3 :m3 :m3 :A3]
   "°b9"      [:m3 :m3 :m3 :M3]         ; diminished minor ninth
   "dimb9"    [:m3 :m3 :m3 :M3]
   "dimmin9"  [:m3 :m3 :m3 :M3]
   "M11"      [:M3 :m3 :M3 :m3 :m3]     ; major eleventh
   "maj11"    [:M3 :m3 :M3 :m3 :m3]
   "11"       [:M3 :m3 :m3 :M3 :m3]     ; dominant eleventh
   "dom11"    [:M3 :m3 :m3 :M3 :m3]
   "mM11"     [:m3 :M3 :M3 :m3 :m3]     ; minor-major eleventh
   "minmaj11" [:m3 :M3 :M3 :m3 :m3]
   "m11"      [:m3 :M3 :m3 :M3 :m3]     ; minor eleventh
   "min11"    [:m3 :M3 :m3 :M3 :m3]
   "+M11"     [:M3 :M3 :m3 :m3 :m3]     ; augmented major eleventh
   "augmaj11" [:M3 :M3 :m3 :m3 :m3]
   "+11"      [:M3 :M3 :d3 :M3 :m3]     ; augmented dominant eleventh
   "aug11"    [:M3 :M3 :d3 :M3 :m3]
   "11#5"     [:M3 :M3 :d3 :M3 :m3]
   "ø11"      [:m3 :m3 :M3 :m3 :M3]     ; half diminished eleventh
   "°11"      [:m3 :m3 :m3 :M3 :m3]     ; diminished eleventh
   "M13"      [:M3 :m3 :M3 :m3 :m3 :M3] ; major thirteenth
   "maj13"    [:M3 :m3 :M3 :m3 :m3 :M3]
   "13"       [:M3 :m3 :m3 :M3 :m3 :M3] ; dominant thirteenth
   "dom13"    [:M3 :m3 :m3 :M3 :m3 :M3]
   "mM13"     [:m3 :M3 :M3 :m3 :m3 :M3] ; minor-major thirteenth
   "minmaj13" [:m3 :M3 :M3 :m3 :m3 :M3]
   "m13"      [:m3 :M3 :m3 :M3 :m3 :M3] ; minor thirteenth
   "min13"    [:m3 :M3 :m3 :M3 :m3 :M3]
   "+M13"     [:M3 :M3 :m3 :m3 :m3 :M3] ; augmented major thirteenth
   "augmaj13" [:M3 :M3 :m3 :m3 :m3 :M3]
   "+13"      [:M3 :M3 :d3 :M3 :m3 :M3] ; augmented dominant thirteenth
   "aug13"    [:M3 :M3 :d3 :M3 :m3 :M3]
   "13#5"     [:M3 :M3 :d3 :M3 :m3 :M3]
   "ø13"      [:m3 :m3 :M3 :m3 :M3 :M3] ; half diminished thirteenth
   "°13"      [:m3 :m3 :m3 :M3 :m3 :M3] ; diminished thirteenth
   })

(defn build-chord
  "Given a base (lowest) note and either:

     - a sequence of intervals like :m3, :P5, etc.
     - the name of a chord, like :Cmaj7 (TODO: more info)

   Returns a list of the notes in the chord, in order from lowest to highest."
  [base-note x]
  (if (coll? x)
    (reductions interval+ base-note x)
    (if-let [[_ bass chord] (re-matches #"([A-G][#b]*)(.*)" (name x))]
      (if-let [intervals (chord-intervals chord)]
        (let [[_ note octave] (re-matches #"([A-G][#b]*)(\d+)" (name base-note))]
          (if (= bass note)
            (build-chord base-note intervals)
            "TODO: check if bass note belongs to chord; if so, build inverted chord"))
        (error (str "Unrecognized chord type: " chord)))
      (error (str "Unrecognized chord symbol: " (name x))))))

