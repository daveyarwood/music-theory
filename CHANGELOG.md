# CHANGELOG

## 0.3.0 (7/4/16)

* Added an `interval-` function to `music-theory.note`. It's like `interval+`, but it subtracts the interval instead of adding it.

* Added a `music-theory.chord` namespace, with two functions:

  * `octave-span` returns the number of octaves spanned by the chord.

  * `build-chord` returns a sequence of notes, giving a starting note like `:C4` and a chord name like `:Cmaj7`. Inverted chords are supported. See the [README](https://github.com/daveyarwood/music-theory#chords) for example usage.

## 0.2.0 (6/29/16)

* Added an `interval+` function in `music-theory.note`.

  See the [README](https://github.com/daveyarwood/music-theory#intervals) for example usage.

## 0.1.0 (6/26/16)

* Initial release.
