Finger trees in Clojurescript

## Overview

Forked from https://github.com/clojure/data.finger-tree
It is finished but test, examples and proper packaging is not done.


Create finger trees with following namespaces:


* ftree.core - basic function common for all finger trees
* ftree.tree - bare tree
* ftree.seqable - seqable tree
* ftree.dl - double list
* ftree.cdl - counted double list


Resources on finger trees:

* https://github.com/Chouser/talk-finger-tree
* http://www.soi.city.ac.uk/~ross/papers/FingerTree.html
* http://blip.tv/clojure/chris-houser-finger-trees-custom-persistent-collections-4632874

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
