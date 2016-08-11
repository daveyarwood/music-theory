(ns music-theory.chord
  (:require [clojure.string    :as    str]
            [music-theory.note :refer (->note interval+ interval-)]
            [music-theory.util :refer (error parse-int)]))

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
   "6"        [:M3 :m3 :M2]             ; major sixth
   "M6"       [:M3 :m3 :M2]
   "maj6"     [:M3 :m3 :M2]
   "m6"       [:m3 :M3 :M2]             ; minor sixth
   "min6"     [:m3 :M3 :M2]
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
   "dim11"    [:m3 :m3 :m3 :M3 :m3]
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
   "dim13"    [:m3 :m3 :m3 :M3 :m3 :M3]
   })

(defn- find-inversion
  "Given a bass (lowest) note and a chord (as a sequence of notes), returns
   either the number of the inversion that would put that note in the bass,
   or nil if that note isn't in the chord.

   e.g.
   (find-inversion \"E\"  [:C4 :E4 :G4]) ;=> 1
   (find-inversion \"F#\" [:C4 :E4 :G4]) ;=> nil"
  [bass chord]
  (first (keep-indexed (fn [i note]
                         (when (str/starts-with? (name note) bass) i))
                       chord)))

(defn octave-span
  "Returns the number of octaves spanned by a chord. The chord must be provided
   as a sequence of notes.

   e.g.
   (octave-span [:D4 :F#4 :A4])         ;=> 1
   (octave-span [:D4 :F#4 :A4 :C#5])    ;=> 1
   (octave-span [:D4 :F#4 :A4 :D5])     ;=> 2
   (octave-span [:D4 :F#4 :A4 :C#5 :E5] ;=> 2
   (octave-span [:C4 :C5 :E5 :G5 :C6]   ;=> 3"
  [chord]
  (let [[min max] ((juxt #(apply min %) #(apply max %))
                   (map #(:number (->note %)) chord))
        span      (- max min)]
    (inc (quot span 12))))

(defn- invert-chord
  [root-chord inversion]
  (concat (drop inversion root-chord)
          (map #(apply interval+ % (repeat (octave-span root-chord) :P8))
               (take inversion root-chord))))

(defn build-chord
  "Given a base (lowest) note and either:

     - a sequence of intervals like :m3, :P5, etc.
     - the name of a chord, like :Cmaj7

   Returns a list of the notes in the chord, in order from lowest to highest."
  [base-note x]
  (if (coll? x)
    (reductions interval+ base-note x)
    (if-let [[_ root chord] (re-matches #"([A-G][#b]*)(.*)" (name x))]
      (if-let [intervals (chord-intervals chord)]
        (let [[_ bass octave] (re-matches #"([A-G][#b]*)(\d+)" (name base-note))]
          (if (= root bass)
            (build-chord base-note intervals)
            (let [root-chord (build-chord (keyword (str root octave)) x)]
              (if-let [inversion (find-inversion bass root-chord)]
                (let [inverted-chord (invert-chord root-chord inversion)
                      octave         (parse-int octave)
                      octave'        (->> inverted-chord
                                          first
                                          name
                                          (re-matches #"[A-G][#b]*(\d+)")
                                          second
                                          parse-int)]
                  (cond
                    (= octave octave')
                    inverted-chord

                    (= octave (inc octave'))
                    (map #(interval+ % :P8) inverted-chord)

                    (= octave (dec octave'))
                    (map #(interval- % :P8) inverted-chord)))
                (error (str "There is no " bass " in a " (name x)))))))
        (error (str "Unrecognized chord type: " chord)))
      (error (str "Unrecognized chord symbol: " (name x))))))

