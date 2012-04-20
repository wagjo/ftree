Finger trees in Clojurescript.

## Overview

Forked from https://github.com/clojure/data.finger-tree

Library is finished but tests, examples and proper packaging is not done.

Resources on finger trees:

* https://github.com/Chouser/talk-finger-tree
* http://www.soi.city.ac.uk/~ross/papers/FingerTree.html
* http://blip.tv/clojure/chris-houser-finger-trees-custom-persistent-collections-4632874

## Usage

Library is not packaged, you should copy ftree source files directly to your project.

List of functions:

* Common for all finger trees - [ftree.core](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/core.cljs)
  * __conjl__ - conj[oin] l[eft]. Returns a new finger tree with the xs 'added'
  to the left.
  * __conjr__ - conj[oin] r[ight]. Returns a new finger tree with the xs
  'added' to the right.
  * __peekl__ - Returns leftmost element.
  * __peekr__ - Returns rightmost element.
  * __popl__ - Returns a new finger tree without leftmost element.
  * __popr__ - Returns a new finger tree without rightmost element.
  * __concat-tree__ - Returns tree which is a concatenation of two given trees.
  * __measured__ - Returns measure of object o. Only for measured trees.
  * __split-tree__ - Returns result of splitting tree based on a predicate. Only for measured trees.
* Bare tree - [ftree.tree](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/tree.cljs)
  * __to-tree__ - Creates finger tree from a seq.
  * __create__ - Creates finger tree containing supplied elements.
* Seqable tree - [ftree.seqable](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/seqable.cljs)
  * __create__ - Creates seqable finger tree containing supplied elements.
  * supports clojure.core __seq, first, rest__
* Double list - [ftree.dl](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/dl.cljs)
  * __create__ - Creates double list containing supplied elements.
  * supports clojure.core __seq, first, rest, conj, peek, pop__
* Counted double list - [ftree.cdl](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/cdl.cljs)
  * __counted-measure__ - Measure to be used with counted double list
  * __create__ - Creates counted double list containing supplied elements.
  * __insert-before__ - Inserts elements into cdl before i position.
  * __remove-at__ - Removes elements starting from i position.
  * __replace-at__ - Replaces elements starting from i position with new ones.
  * __update-at__ - Updates element at i position with result of calling update-fn.
  * __dropl__ - Returns a new finger tree without n leftmost elements.
  * __dropr__ - Returns a new finger tree without n rightmost elements.
  * __split-at__ - Returns splitted tree at i position. i position will be in the
  second tree.
  * supports clojure.core __seq, first, rest, conj, peek, pop, count, nth, assoc__
* Setting measure - [ftree.measure](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/measure.cljs)
  * __set-measure!__ - Sets new measure. Returns the old one.
  * __with-measure__ - macro defined in [ftree/measure.clj](https://github.com/wagjo/ftree/blob/master/src/clj/ftree/measure.clj)
  
## Examples

    (ns foo.bar
      (:require [ftree.measure :as fm]
                [ftree.cdl :as fcdl]
                [ftree.core :as ftc]))

    ...

    (fm/set-measure! fcdl/counted-measure)

    (let [x (fcdl/create :a :b :c :d :e :f)]
      [x (first x) (rest x)])
    
    (let [x (fcdl/create :a :b :c :d :e :f)]
      [(ftc/conjl x 1) (ftc/conjr x 1) (ftc/peekl x) (ftc/popr x)])
    
    (let [x (fcdl/create :a :b :c :d :e :f :g :h :i :j :k :l :m :n :o)]
      [(fcdl/replace-at x 13 20 [:X :Y :Z])
       (fcdl/dropr x 4)
       (fcdl/insert-before x 10 :X :Y :Z)])

## License

Copyright (C) 2012, Rich Hickey, Chris Houser, Jozef Wagner.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 
(http://opensource.org/licenses/eclipse-1.0.php) which can be found
 in the file epl-v10.html at the root of this distribution.

By using this software in any fashion, you are agreeing to be bound
by the terms of this license.

You must not remove this notice, or any other, from this software.
