(set-env!
  :source-paths #{"src" "test"}
  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [adzerk/bootlaces    "0.1.12" :scope "test"]
                  [adzerk/boot-test    "1.0.4"  :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-test :refer :all])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
  pom {:project 'music-theory
       :version +version+
       :description "A music theory library for Clojure/ClojureScript"
       :url "https://github.com/daveyarwood/music-theory"
       :scm {:url "https://github.com/daveyarwood/music-theory"}
       :license {"name" "Eclipse Public License"
                 "url" "http://www.eclipse.org/legal/epl-v10.html"}}
  test {:namespaces '#{music-theory.pitch-test}})
