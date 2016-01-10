(ns music-theory.note)

(defrecord Note [number])

(def intervals
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
            base-note (+ (intervals letter) (* octave 12) 12)]
        (->Note (reduce (fn [note-number accidental]
                          (case accidental
                            \# (inc note-number)
                            \b (dec note-number)))
                        base-note
                        accs)))
      (throw (new #?(:clj  Exception
                     :cljs js/Error)
                  "Invalid note format.")))))

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
               (:number (->note note)))]
    (quot (- note 12) 12)))

(defn note-position
  "Given a tonic (e.g. A) and a note (e.g. C#), returns a number from 0-11
   representing the position of the note (e.g. 4) relative to the tonic."
  [tonic note]
  (let [tonic-n (-> tonic name (str 1) ->note :number)
        note-n  (-> note  name (str 1) ->note :number)
        note-n  (if (< note-n tonic-n)
                  (+ note-n 12)
                  note-n)]
    (- note-n tonic-n)))

