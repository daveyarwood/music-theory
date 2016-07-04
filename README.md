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
boot.user=> (require '[music-theory.note :refer (interval+ interval-)])
nil

; "interval+" takes a note and an interval and returns the note that interval
; above the note provided.
boot.user=> (interval+ :G#4 :m3)
:B4
boot.user=> (interval+ :Ab4 :m3)
:Cb5
boot.user=> (interval+ :C4 :P8)
:C5
boot.user=> (interval+ :C4 :P5)
:G4

; "interval-" does the same thing, but returns the note that interval BELOW the
; note.
boot.user=> (interval- :G#4 :m3)
:E#4
boot.user=> (interval- :Ab4 :m3)
:F4
boot.user=> (interval- :C4 :P8)
:C3
boot.user=> (interval- :C4 :P5)
:F3

; Intervals up to :P15 (perfect 15th / double octave) are supported.

; You can also use "interval+" and "interval-" with MIDI note numbers:
boot.user=> (interval+ 60 :P4)
65
boot.user=> (interval- 60 :P8)
48

; "interval+" and "interval-" take a variable number of arguments; all of the
; intervals are added to/subtracted from the note:
boot.user=> (interval+ 60 :P8 :P4)
77
boot.user=> (interval+ :C4 :P8 :P4)
:F5
boot.user=> (interval+ :C4 :P8 :P8 :P8 :P8)
:C8
boot.user=> (interval- :C4 :P8 :P8 :P8 :P8)
:C0
```

#### Chords

```clojure
boot.user=> (require '[music-theory.chord :refer (octave-span build-chord)])
nil

; "build-chord" takes two arguments and returns a list of notes representing a
; chord. The first argument is a base note, including the octave; this will be
; the lowest note in the chord.
;
; The second argument can be a sequence of intervals to add onto the base note.
; For example, a major chord consists of a major third + a minor third.
boot.user=> (build-chord :Bb3 [:M3 :m3])
(:Bb3 :D4 :F4)

; A dominant seventh chord is a major chord with another minor third on top.
boot.user=> (build-chord :Bb3 [:M3 :m3 :m3])
(:Bb3 :D4 :F4 :Ab4)

; The second argument to "build-chord" can also be a chord name. A variety of
; chord types are supported.
boot.user=> (build-chord :Bb3 :Bb7)
(:Bb3 :D4 :F4 :Ab4)
boot.user=> (build-chord :C4 :C)
(:C4 :E4 :G4)
boot.user=> (build-chord :C4 :Cm)
(:C4 :Eb4 :G4)
boot.user=> (build-chord :C4 :Cm7)
(:C4 :Eb4 :G4 :Bb4)
boot.user=> (build-chord :C4 :C7)
(:C4 :E4 :G4 :Bb4)
boot.user=> (build-chord :C4 :Cmaj7)
(:C4 :E4 :G4 :B4)
boot.user=> (build-chord :C4 :C13)
(:C4 :E4 :G4 :Bb4 :D5 :F5 :A5)
boot.user=> (build-chord :C4 :Cdim13)
(:C4 :Eb4 :Gb4 :Bbb4 :Db5 :Fb5 :Ab5)
boot.user=> (build-chord :C4 :Cdim)
(:C4 :Eb4 :Gb4)
boot.user=> (build-chord :C4 :C6)
(:C4 :E4 :G4 :A4)
boot.user=> (build-chord :C4 :C5)
(:C4 :G4)
boot.user=> (build-chord :C4 :C+7)
(:C4 :E4 :G#4 :Bb4)
boot.user=> (build-chord :C4 :C7b5)
(:C4 :E4 :Gb4 :Bb4)
boot.user=> (build-chord :C4 :Caug)
(:C4 :E4 :G#4)

; Inverted chords are also supported:
boot.user=> (build-chord :C4 :Cmaj7)
(:C4 :E4 :G4 :B4)
boot.user=> (build-chord :E4 :Cmaj7)
(:E4 :G4 :B4 :C5)
boot.user=> (build-chord :G4 :Cmaj7)
(:G4 :B4 :C5 :E5)
boot.user=> (build-chord :B4 :Cmaj7)
(:B4 :C5 :E5 :G5)

; An exception is thrown if you try to build an inverted chord on a note that
; doesn't belong to the chord:
boot.user=> (build-chord :F4 :Cmaj7)
java.lang.Exception: There is no F in a Cmaj7

; "octave-span" tells you how many octaves are included in the chord. For
; example, "smaller" chords like minor, major, sixth and seventh chords all
; fit inside of one octave, so their octave span is 1. "Bigger" extended
; chords like ninths, elevenths and thirteenths have a larger octave span.
boot.user=> (octave-span [:C4 :G4])
1
boot.user=> (octave-span [:C4 :G4 :C5])
2
boot.user=> (octave-span [:C4 :G4 :C5 :C6])
3
boot.user=> (octave-span (build-chord :C4 :C))
1
boot.user=> (octave-span (build-chord :C4 :C7))
1
boot.user=> (octave-span (build-chord :C4 :C9))
2
boot.user=> (octave-span (build-chord :C4 :C13))
2
```

## Contributing

Pull requests welcome!

## License

Copyright © 2015-2016 Dave Yarwood

Distributed under the Eclipse Public License version 1.0.
