(ns music-theory.pitch)

(def ^:dynamic *reference-pitch* 440)

(defn midi->hz
  "Converts a MIDI note (0-127) to its frequency in Hz.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [midi-note]
  {:pre [(integer? midi-note)
         (<= 0 midi-note 127)]}
  (* *reference-pitch* (Math/pow 2 (/ (- midi-note 69) 12.0))))

(defn hz->midi
  "Converts a frequency in Hz to the closest MIDI note.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [freq]
  {:pre  [(number? freq) (pos? freq)]
   :post [(not (neg? %))]}
  (letfn [(log2 [n] (/ (Math/log n) (Math/log 2)))]
    (Math/round (+ 69 (* 12 (log2 (/ freq *reference-pitch*)))))))
