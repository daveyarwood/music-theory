(ns music-theory.note
  (:require [music-theory.util :refer (error)]))

(defrecord Note [number])

(def ^:private note->interval
  {"C" 0, "D" 2, "E" 4, "F" 5, "G" 7, "A" 9, "B" 11
   "c" 0, "d" 2, "e" 4, "f" 5, "g" 7, "a" 9, "b" 11})

(defn ->note
  "Creates a Note record, which represents a note as an unbounded MIDI note
   number, from a string or keyword describing the note in scientific pitch
   notation, i.e. a letter and (optionally) any number of sharps and flats.

   e.g. C#5, Dbb4, E0"
  [x]
  (let [s (name x)
        [letter accs octave] (rest (re-matches #"([A-Ga-g])([#b]*)(-?\d+)" s))]
    (if (and letter accs octave)
      (let [octave (#?(:clj  Integer/parseInt
                       :cljs js/Number)
                    octave)
            base-note (+ (note->interval letter) (* octave 12) 12)]
        (->Note (reduce (fn [note-number accidental]
                          (case accidental
                            \# (inc note-number)
                            \b (dec note-number)))
                        base-note
                        accs)))
      (error "Invalid note format."))))

(defn note->midi
  "Converts a note in the form of a string or keyword (e.g. C#4, :Db5, A2) into
   the corresponding MIDI note number.

   Throws an assertion error if the note is outside the range of MIDI notes
   (0-127)."
  [note]
  {:post [(<= 0 % 127)]}
  (:number (->note note)))

(defn octave
  "Returns the octave of a note."
  [note]
  (let [note (if (number? note)
               note
               (:number (->note note)))
        ; adjust for weird skew caused by integer division on negative numbers
        note (if (< note 12)
               (- note 11)
               note)]
    (quot (- note 12) 12)))

(defn note-position
  "Given a tonic (e.g. A) and a note (e.g. C#), returns a number from 0-11
   representing the position of the note (e.g. 4) relative to the tonic."
  [tonic note]
  (let [tonic-n (-> tonic name (str 1) ->note :number)
        note-n  (if (number? note)
                  ; get the number of that note in octave 1 (24-35)
                  (if-not (<= 24 note 35)
                    (+ 12 (rem note 24))
                    note)
                  (-> note name (str 1) ->note :number))
        note-n  (if (< note-n tonic-n)
                  (+ note-n 12)
                  note-n)]
    (- note-n tonic-n)))

(defn spell-note
  "Given a letter (as a capital character like \\D) and a note number (e.g. 61
   is one semitone above middle C), returns the correct enharmonic spelling of
   the note (in this case, :Db4), built on that note."
  [note-letter note-number]
  (let [base-note        (note->interval (str note-letter))
        octaves          (iterate (fn [[n oct]]
                                    [(+ n 12) (inc oct)])
                                  [base-note -1])
        [closest octave] (first (drop-while (fn [[n oct]]
                                              (> (- note-number n) 6))
                                            octaves))
        delta            (- note-number closest)
        accidentals      (apply str (if (pos? delta)
                                      (repeat delta \#)
                                      (repeat (- delta) \b)))]
    (keyword (str note-letter accidentals octave))))

(def ^:private interval->semitones
  {:P1  0  ; perfect unison
   :m2  1  ; minor second
   :M2  2  ; major second
   :d3  2  ; diminished third
   :m3  3  ; minor third
   :M3  4  ; major third
   :A3  5  ; augmented third
   :P4  5  ; perfect fourth
   :A4  6  ; augmented fourth
   :d5  6  ; diminished fifth
   :P5  7  ; perfect fifth
   :m6  8  ; minor sixth
   :M6  9  ; major sixth
   :m7  10 ; minor seventh
   :M7  11 ; major seventh
   :P8  12 ; perfect octave

   :m9  13 ; minor ninth
   :M9  14 ; major ninth
   :m10 15 ; minor tenth
   :M10 16 ; major tenth
   :P11 17 ; perfect eleventh
   :A11 18 ; augmented eleventh
   :d12 18 ; diminished twelfth
   :P12 19 ; perfect twelfth
   :m13 20 ; minor thirteenth
   :M13 21 ; major thirteenth
   :m14 22 ; minor fourteenth
   :M14 23 ; major fourteenth
   :P15 24 ; perfect fifteenth (double octave)
   })

(defn letter+
  "Given a letter (as a capital character, like \\A) and an interval to move
   up, returns the resulting letter (A-G), ignoring accidentals.

   e.g. F + 1 == F (unison)
        F + 2 == G (2nd)
        F + 3 == A (3rd)
        F + 4 == B (4th)
        F + 8 == F (octave)"
  [letter interval]
  (let [letters (drop-while (partial not= letter) (cycle "ABCDEFG"))]
    (nth letters (dec interval))))

(defn interval+
  "Given a note (spelled a particular way, e.g. \"Ab4\" or \"G#4\") and an
   interval (which can be something like :m3 for minor third, :M3 for major
   third, etc.), returns the correctly spelled note that interval above that
   note.

   Can take multiple intervals, all of which will be added to the note.

   When given a MIDI note number, returns a MIDI note number instead of a note."
  [note & intervals]
  (letfn [(note+interval [note interval]
            (if-let [interval-semitones (interval->semitones interval)]
              (if (number? note)
                (+ note interval-semitones)
                (let [note-number (-> note
                                      ->note
                                      :number
                                      (+ interval-semitones))
                      steps       (-> interval
                                      name
                                      (subs 1)
                                      #?(:clj  Integer/parseInt
                                         :cljs js/Number))
                      note-letter (letter+ (first (name note)) steps)]
                  (spell-note note-letter note-number)))
              (error (str "Invalid interval: " interval))))]
    (reduce note+interval note intervals)))

