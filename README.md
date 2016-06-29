# music-theory

[![Clojars Project](https://img.shields.io/clojars/v/music-theory.svg)](https://clojars.org/music-theory)

A music theory library for Clojure and ClojureScript, providing the systematic logic of [music theory](https://en.wikipedia.org/wiki/Music_theory) through helper functions.

## Usage

### API docs

*TODO: [generate API docs](https://github.com/daveyarwood/music-theory/issues/11). For now, see the docstrings in the source code.*

### Examples

#### Note number/octave

```clojure
boot.user=> (require '[music-theory.note :refer (note->midi octave)])
nil

; "note" (letter + octave) -> MIDI note number
boot.user=> (note->midi :A4)
69
boot.user=> (note->midi "C#3")
49

; note -> octave number
boot.user=> (octave "A4")
4
boot.user=> (octave :C#3)
3

; MIDI note -> octave number
boot.user=> (octave (note->midi "A4"))
4
boot.user=> (octave (+ 12 (note->midi "A4")))
5
```

#### Note frequency

```clojure
boot.user=> (require '[music-theory.pitch :refer :all])
nil

; reference pitch (in Hz) is the pitch of the note A4
; (A in the 4th octave, in scientific pitch notation)
;
; A440 (A4 = 440 Hz) is the default reference pitch
boot.user=> *reference-pitch*
440

; MIDI note -> pitch in Hz
boot.user=> (midi->hz (note->midi "A4"))
440.0
boot.user=> (midi->hz (note->midi "Bb4"))
466.1637615180899
boot.user=> (midi->hz (note->midi "Bb5"))
932.3275230361799

; note -> pitch in Hz
boot.user=> (note->hz :A4)
440.0
boot.user=> (note->hz :Bb4)
466.1637615180899

; pitch in Hz -> MIDI note number
boot.user=> (hz->midi 500)
71

; create a scope with a different reference pitch
boot.user=> (with-reference-pitch 430
       #_=>   (midi->hz (note->midi "A4")))
430.0

; outside of the scope, the reference pitch is still the same
boot.user=> *reference-pitch*
440
boot.user=> (midi->hz (note->midi "A4"))
440.0

; change the default reference pitch
boot.user=> (set-reference-pitch! 430)
430
boot.user=> *reference-pitch*
430
boot.user=> (midi->hz (note->midi "A4"))
430.0
boot.user=> (midi->hz (note->midi "Bb4"))
455.56913057449697
boot.user=> (midi->hz (note->midi "A#5"))
911.1382611489939

boot.user=> (set-reference-pitch! 440)
440
```

#### Note duration

```clojure
boot.user=> (require '[music-theory.duration :refer :all])
nil

; note values (American names)
boot.user=> DOUBLE-WHOLE
0.5
boot.user=> WHOLE
1
boot.user=> HALF
2
boot.user=> QUARTER
4
; ...
boot.user=> HUNDRED-TWENTY-EIGHTH
128

; note values (British names)
boot.user=> BREVE
0.5
boot.user=> SEMIBREVE
1
boot.user=> MINIM
2
boot.user=> CROTCHET
4
boot.user=> QUAVER
8
; ...
boot.user=> SEMIHEMIDEMISEMIQUAVER
128

; The note values above represent fractions of a measure of 4/4.
; For example, a quarter note (4) takes up 1/4 of a measure of 4/4.
; In other words, it takes 4 quarter notes to fill a measure of 4/4.

; Custom note values can be derived by adding existing note values together
; with "note-length+".

boot.user=> (note-length+ QUARTER QUARTER QUARTER)
1.3333333333333333
boot.user=> (note-length+ QUARTER QUARTER)
2.0
boot.user=> (== HALF (note-length+ QUARTER QUARTER))
true

; The "beats" function takes a variable number of note values and returns the
; total number of beats.
boot.user=> (beats QUARTER)
1.0
boot.user=> (beats QUARTER QUARTER)
2.0

; A note value can be either a number (e.g. one of the note value constants above)
; or a string that can contain a number and any number of dots.
boot.user=> (beats "2.")
3.0

; Dots can also be added via the "dots" function.
boot.user=> (beats "2." 4)
4.0
boot.user=> (beats (dots 1 HALF) QUARTER)
4.0
boot.user=> (= (beats "4..") (beats (dots 2 QUARTER)))
true
boot.user=> (dots 1 QUARTER)
2.6666666666666665
boot.user=> (beats (dots 1 QUARTER))
1.5
boot.user=> (beats (dots 1 QUARTER) (dots 2 HALF))
5.0

; Tuplet durations are possible via the "tuplet" function and derived functions
; like "triplet."
boot.user=> (tuplet 3/2 QUARTER)
6.0
boot.user=> (tuplet 3/2 QUARTER QUARTER QUARTER)
2.0
boot.user=> (beats (triplet QUARTER QUARTER QUARTER))
2.0

; "measure?" returns true if the supplied list of note lengths exactly fills one
; measure of the supplied time signature.
boot.user=> (measure? 3/4 [(dots 1 HALF)])
true
boot.user=> (measure? 3/4 [(dots 1 HALF) QUARTER])
false
```

#### Intervals

```clojure
; "interval+" takes a note and an interval and returns the note that interval
; above the note provided.
boot.user=> (require '[music-theory.note :refer (interval+)])
nil
boot.user=> (interval+ :G#4 :m3)
:B4
boot.user=> (interval+ :Ab4 :m3)
:Cb5
boot.user=> (interval+ :C4 :P8)
:C5
boot.user=> (interval+ :C4 :P5)
:G4

; Intervals up to `:P15` (perfect 15th / double octave) are supported.

; You can also use `interval+` with MIDI note numbers:
boot.user=> (interval+ 60 :P4)
65
boot.user=> (interval+ 60 :P8)
72

; "interval+" takes a variable number of arguments; all of the intervals are added to the note:
boot.user=> (interval+ 60 :P8 :P4)
77
boot.user=> (interval+ :C4 :P8 :P4)
:F5
boot.user=> (interval+ :C4 :P8 :P8 :P8 :P8)
:C8
```

## Contributing

Pull requests welcome!

## License

Copyright © 2015-2016 Dave Yarwood

Distributed under the Eclipse Public License version 1.0.
