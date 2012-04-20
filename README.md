Finger trees in Clojurescript

## Overview

Forked from https://github.com/clojure/data.finger-tree
It is finished but tests, examples and proper packaging is not done.

Resources on finger trees:

* https://github.com/Chouser/talk-finger-tree
* http://www.soi.city.ac.uk/~ross/papers/FingerTree.html
* http://blip.tv/clojure/chris-houser-finger-trees-custom-persistent-collections-4632874

## Quick overview

* Common for all finger trees - [ftree.core](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/core.cljs)
  * _conjl_ - conj[oin] l[eft]. Returns a new finger tree with the xs 'added'
  to the left.
  * _conjr_ - conj[oin] r[ight]. Returns a new finger tree with the xs
  'added' to the right.
  * _peekl_ - Returns leftmost element.
  * _peekr_ - Returns rightmost element.
  * _popl_ - Returns a new finger tree without leftmost element.
  * _popr_ - Returns a new finger tree without rightmost element.
  * _concat-tree_ - Returns tree which is a concatenation of two given trees.
  * _measured_ - Returns measure of object o. Only for measured trees.
  * _split-tree_ - Returns result of splitting tree based on a predicat.
* Bare tree - [ftree.tree](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/tree.cljs)
  * _empty-tree_
  * _to-tree_ - Creates finger tree from a seq.
  * _create_ - Creates finger tree containing supplied elements.
  * supports clojure.core 
* Seqable tree - [ftree.seq](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/seq.cljs)
  * _create_ - Creates seqable finger tree containing supplied elements.
  * supports clojure.core seq, first, rest, 
* Double list - [ftree.dl](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/dl.cljs)
  * _create_ - Creates double list containing supplied elements.
  * supports clojure.core seq, first, rest, conj, peek, pop
* Counted double list - [ftree.cdl](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/cdl.cljs)
  * _counted-measure_ - Measure to be used with counted double list
  * _create_ - Creates counted double list containing supplied elements.
  * _insert-before_ - Inserts elements into cdl before i position.
  * _remove-at_ - Removes elements starting from i position.
  * _replace-at_ - Replaces elements starting from i position with new ones.
  * _update-at_ - Updates element at i position with result of calling update-fn.
  * _dropl_ - Returns a new finger tree without n leftmost elements.
  * _dropr_ - Returns a new finger tree without n rightmost elements.
  * _split-at_ - Returns splitted tree at i position. i position will be in the
  second tree.
  * supports clojure.core seq, first, rest, conj, peek, pop, count, nth, assoc
* Setting measure - [ftree.measure](https://github.com/wagjo/ftree/blob/master/src/cljs/ftree/measure.cljs)
  * _set-measure!_ - Sets new measure. Returns the old one.
  * _with-measure_ - macro defined in [ftree/measure.clj](https://github.com/wagjo/ftree/blob/master/src/clj/ftree/measure.cljs)
  
## Usage

    (:require [ftree.measure :as fm]
              [ftree.cdl :as fcdl]
              [ftree.core :as ftc])

    ...

    (fm/set-measure! fcdl/counted-measure)

    (let [t (cdl/create 1 2 3 4 5 6 7)])
    
    (let [x (ftree.dl/create :a :b :c :d :e :f)]
      [x
       (first x)
       (rest x)])
    
    (let [x (ftree.cdl/create :a :b :c :d :e :f :g :h :i :j :k :l :m :n :o)]
      [(cdl/replace-at x 13 20 [:X :Y :Z])
       (cdl/dropr x 4)
       (cdl/insert-before x 10 :X :Y :Z)])

## License

Copyright (C) 2012, Rich Hickey, Chris Houser, Jozef Wagner.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 
(http://opensource.org/licenses/eclipse-1.0.php) which can be found
 in the file epl-v10.html at the root of this distribution.

By using this software in any fashion, you are agreeing to be bound
by the terms of this license.

You must not remove this notice, or any other, from this software.
