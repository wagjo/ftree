;; Copyright (c) Rich Hickey, Chris Houser. All rights reserved.
;; Copyright (C) 2012, Jozef Wagner. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be
;; found in the file epl-v10.html at the root of this distribution.
;;
;; By using this software in any fashion, you are agreeing to be bound
;; by the terms of this license.
;;
;; You must not remove this notice, or any other, from this software.

(ns ftree.cdl
  "Counted Double List."
  (:refer-clojure :exclude [split-at])
  (:require [ftree.exception :as ex]
            [ftree.measure :as measure]
            [ftree.tree :as ft]
            [ftree.protocol :as ftp]
            [ftree.core :as ftc]))

;;;; Implementation details

(deftype CountedDoubleList [tree]
  ISeqable
  (-seq [t] (when (not (ftp/-empty-tree? tree)) t))
  ISequential
  ISeq
  (-first [t] (ftp/-peekl t))
  (-rest [t] (ftp/-popl t))
  ICollection
  (-conj [t o] (ftp/-conjr t o))
  IStack
  (-peek [t] (ftp/-peekr t))
  (-pop [t] (ftp/-popr t))
  ICounted
  (-count [_] (ftp/-measured tree))
  IAssociative
  (-contains-key? [_ k] (< -1 k (ftp/-measured tree)))
  (-assoc [t k v] (if (-contains-key? t k)
                   (let [[pre mid post] (ftc/split-tree t #(> % k))]
                     (ftp/-app3 pre v post))
                   (throw ex/index-out-of-bounds)))
  IIndexed
  (-nth [t n] (if (-contains-key? t n)
                (second (ftc/split-tree tree #(> % n)))
                (throw ex/index-out-of-bounds)))
  (-nth [t n not-found] (if (-contains-key? t n)
                          (second (ftc/split-tree tree #(> % n)))
                          not-found))
  IPrintable
  (-pr-seq [t _] (concat ["("] (interpose " " (map pr-str t)) [")"]))
  #_(-pr-seq [_ _2] (list "#<CDoubleList " (pr-str tree) " >"))
  ftp/IInsertRemove
  (-insert-before [t i xs]
                  (if (seq xs)
                    (if (== i (ftp/-measured tree))
                      ;; inserting at the end
                      (apply ftc/conjr t xs)
                      ;; inserting before end
                      (if (-contains-key? t i)
                        (let [[pre mid post]
                              (ftc/split-tree t #(> % i))]
                          (ftp/-app3 (apply ftc/conjr pre xs)
                                     [mid] post))
                        (throw ex/index-out-of-bounds)))
                    t))
  (-remove-at [t i] (ftp/-remove-at t i 1))
  (-remove-at [t i count]
    (if (and (-contains-key? t i)
             (< 0 count))
      (let [[first-part _ interm]
            (ftc/split-tree t #(> % i))] ;; find i-th element
        (if (ftp/-empty-tree? interm)
          first-part ;; was last one
          (if (== count 1)
            ;; should remove just one element
            (ftc/concat-tree first-part interm) 
            (let [newcount (- count 1)]
              (if (>= newcount (ftp/-measured interm))
                first-part ;; should remove all remaining elements
                (let [[_ _2 second-part]
                      (ftc/split-tree interm #(>= % newcount))]
                  ;; concat with remaining part
                  (ftc/concat-tree first-part second-part)))))))
      (throw ex/index-out-of-bounds)))  
  (-replace-at [t i xs] (ftp/-replace-at t i 1 xs))
  (-replace-at [t i count xs]
    (if (and (-contains-key? t i)
             (< 0 count))
      (let [[first-part _ interm]
            (ftc/split-tree t #(> % i))] ;; find i-th element
        (if (ftp/-empty-tree? interm)
          (apply ftc/conjr first-part xs) ;; was last one
          (if (== count 1)
            ;; should replace just one element
            (ftp/-app3 first-part (seq xs) interm) 
            (let [newcount (- count 1)]
              (if (>= newcount (ftp/-measured interm))
                ;; should replace all remaining elements
                (apply ftc/conjr first-part xs) 
                (let [[_ _2 second-part]
                      (ftc/split-tree interm #(>= % newcount))]
                  ;; concat with remaining part
                  (ftp/-app3 first-part (seq xs) second-part)))))))
      (throw ex/index-out-of-bounds)))  
  (-update-at [t i update-fn args]
    (if (-contains-key? t i)
      (let [[first-part old-value interm]
            (ftc/split-tree t #(> % i))] ;; find i-th element
        (if (ftp/-empty-tree? interm)
          (apply ftc/conjr first-part (apply update-fn old-value args)) ;; was last one
          (ftp/-app3 first-part
                     (apply update-fn old-value args) interm)))
      (throw ex/index-out-of-bounds)))  
  ftp/ICountedDrop
  (-dropl [t n] (if (>= n (ftp/-measured tree))
                  (CountedDoubleList. ft/empty-tree)
                  (if (-contains-key? t n)
                    (let [[lt x rt] (ftc/split-tree t #(> % n))]
                      (ftp/-conjl rt x))
                    (throw ex/index-out-of-bounds))))
  (-dropr [t n] (if (>= n (ftp/-measured tree))
                  (CountedDoubleList. ft/empty-tree)
                  (if (-contains-key? t n)
                    (let [nn (- (ftp/-measured tree) n 1)
                          [lt x rt] (ftc/split-tree t #(> % nn))]
                      (ftp/-conjr lt x))
                    (throw ex/index-out-of-bounds))))
  ftp/IEmptyTree
  (-empty-tree? [_] (ftp/-empty-tree? tree))
  ftp/IDeque
  (-conjl [_ o] (CountedDoubleList. (ftp/-conjl tree o)))
  (-conjr [_ o] (CountedDoubleList. (ftp/-conjr tree o)))
  (-peekl [_] (ftp/-peekl tree))
  (-peekr [_] (ftp/-peekr tree))
  (-popl [_] (CountedDoubleList. (ftp/-popl tree)))
  (-popr [_] (CountedDoubleList. (ftp/-popr tree)))
  ftp/IMeasured
  (-measured [_] (ftp/-measured tree))
  ftp/IMeasuredTree
  (-measured-popl [_] (ftp/-measured-popl tree))
  (-measured-popr [_] (ftp/-measured-popr tree))
  ftp/ISplittableTree
  (-split [_ pred acc] (let [[lt xs rt] (ftp/-split tree pred acc)]
                         [(CountedDoubleList. lt)
                          xs
                          (CountedDoubleList. rt)]))
  ftp/IConcatenableTree
  (-app3 [_ ts t2] (ftp/-app3 tree ts t2))
  (-app3deep [_ ts t1] (CountedDoubleList. (ftp/-app3deep tree ts t1))))

;;;; Public API

(def ^{:doc "Measure to be used with counted double list"}  
  counted-measure (measure/Measure. 0 + (constantly 1)))

(defn create
  "Creates counted double list.
  You have to set the correct measure before using this list.
  Returns created ftree."
  [& xs]
  (CountedDoubleList. (ft/to-tree xs)))

(defn insert-before
  "Inserts elements into cdl before i position.
  Returns updated ftree."
  [cdl i xs]
  (ftp/-insert-before cdl i xs))

(defn remove-at
  "Removes elements starting from i position.
  Returns updated ftree."
  ([cdl i] (ftp/-remove-at cdl i))
  ([cdl i count] (ftp/-remove-at cdl i count)))

(defn replace-at
  "Replaces elements starting from i position with new ones.
  Returns updated ftree."
  ([cdl i xs] (ftp/-replace-at cdl i xs))
  ([cdl i count xs] (ftp/-replace-at cdl i count xs)))

(defn update-at
  "Updates element at i position with result of calling update-fn.
  Returns updated ftree."
  ([cdl i update-fn & args]
     (ftp/-update-at cdl i update-fn args)))

(defn dropl
  "Returns a new finger tree without n leftmost elements.
  Returns updated ftree."
  [coll n]
  (ftp/-dropl coll n))

(defn dropr
  "Returns a new finger tree without n rightmost elements.
  Returns updated ftree."
  [coll n]
  (ftp/-dropr coll n))

(defn split-at
  "Returns splitted tree at i position. i position will be in the
  second tree. Returns pair of trees."
  [t i]
  (condp = i
    (count t)
    [t (create)]
    0
    [(create) t]
    (let [[pre mid pos] (ftc/split-tree t #(> % i))]
      [pre (ftc/conjl pos mid)])))
