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

(ns ftree.tree
  "Basic finger trees."
  (:require-macros [ftree.tree :as ftm])
  ;; following aliases are also used inside macros
  (:require [ftree.protocol :as ftp]
            [ftree.measure :as measure]
            [ftree.exception :as ex]
            [ftree.core :as ftc]))

;; Cannot split various tree definitions into separate files because
;; of heavy use of forward declarations. 

;;;; Digit

(declare empty-tree create-digit)

(ftm/defdigit a)
(ftm/defdigit a b)
(ftm/defdigit a b c)
(ftm/defdigit a b c d)

(defn create-digit
  "Creates Digit object based on number of items.
  Returns newly created object."
  ([a]       (ftm/make-digit a))
  ([a b]     (ftm/make-digit a b))
  ([a b c]   (ftm/make-digit a b c))
  ([a b c d] (ftm/make-digit a b c d)))

;;;; EmptyTree

(declare create-single-tree)

(deftype EmptyTree []
  ISeqable
  (-seq [_] nil) ; needed for split ops
  IPrintable
  (-pr-seq [_ _2] (list "#<EFT[" (pr-str measure/-empty-tree-measure)
                        "]>"))
  ftp/IEmptyTree
  (-empty-tree? [_] true)
  ftp/IDeque
  (-conjl [_ o] (create-single-tree o))
  (-conjr [_ o] (create-single-tree o))
  (-peekl [_] nil)
  (-peekr [_] nil)
  (-popl [t] t)
  (-popr [t] t)
  ftp/IMeasured
  (-measured [_] measure/-empty-tree-measure)
  ftp/IMeasuredTree
  (-measured-popl [_] measure/-empty-tree-measure)
  (-measured-popr [_] measure/-empty-tree-measure)
  ftp/ISplittableTree
  (-split [_ _2 _3] (throw ex/unsupported-operation))
  ftp/IConcatenableTree
  (-app3 [_ ts t2] (reduce ftp/-conjl t2 (reverse ts)))
  (-app3deep [_ ts t1] (reduce ftp/-conjr t1 ts)))

;;;; SingleTree

(declare create-deep-tree)

(deftype SingleTree [x]
  IPrintable
  (-pr-seq [_ _2] (list "#<SFT[" (pr-str (ftp/-measured x))
                        "] " (pr-str x) ">"))
  ftp/IEmptyTree
  (-empty-tree? [_] false)
  ftp/IDeque
  (-conjl [_ o] (create-deep-tree (create-digit o) empty-tree
                                  (create-digit x)))
  (-conjr [_ o] (create-deep-tree (create-digit x) empty-tree
                                  (create-digit o)))
  (-peekl [_] x)
  (-peekr [_] x)
  (-popl [_] empty-tree)
  (-popr [_] empty-tree)
  ftp/IMeasured
  (-measured [_] (ftp/-measured x))
  ftp/IMeasuredTree
  (-measured-popl [_] measure/-empty-tree-measure)
  (-measured-popr [_] measure/-empty-tree-measure)
  ftp/ISplittableTree
  (-split [_ _2 _3] [empty-tree x empty-tree])
  ftp/IConcatenableTree
  (-app3 [_ ts t2] (ftp/-app3 empty-tree (cons x ts) t2))
  (-app3deep [_ ts t1] (ftp/-conjr (ftp/-app3deep empty-tree ts t1)
                                   x)))

(defn create-single-tree
  "Creates single-tree with element x.
  Returns created ftree."
  [x]
  (SingleTree. x))

;;;; DelayedTree

(deftype DelayedTree [tree-ref measure-val]
  IPrintable
  (-pr-seq [_ _2] (list "#<DelFT[" (pr-str measure-val)
                        "] " (pr-str @tree-ref) " >"))
  ftp/IEmptyTree
  (-empty-tree? [_] (ftp/-empty-tree? @tree-ref))
  ftp/IDeque
  (-conjl [_ o] (ftp/-conjl @tree-ref o))
  (-conjr [_ o] (ftp/-conjr @tree-ref o))
  (-peekl [_] (ftp/-peekl @tree-ref))
  (-peekr [_] (ftp/-peekr @tree-ref))
  (-popl [_] (ftp/-popl @tree-ref))
  (-popr [_] (ftp/-popr @tree-ref))
  ftp/IMeasured
  (-measured [_] measure-val)
  ftp/IMeasuredTree
  (-measured-popl [_] (ftp/-measured-popl @tree-ref))
  (-measured-popr [_] (ftp/-measured-popr @tree-ref))
  ftp/ISplittableTree
  (-split [_ pred acc] (ftp/-split @tree-ref pred acc))
  ftp/IConcatenableTree
  (-app3 [_ ts t2] (ftp/-app3 @tree-ref ts t2))
  (-app3deep [_ ts t1] (ftp/-app3deep @tree-ref ts t1)))

;; NOTE: create-delayed-tree macro defined in ftree/tree.clj

;;;; DeepTree

(declare to-tree)

(defn- deep-left
  "Creates deep finger tree from pre mid suf,
  where pre (and mid) is possibly an empty tree."
  [pre mid suf]
  (cond
   (not (ftp/-empty-tree? pre)) (create-deep-tree pre mid suf)
   (ftp/-empty-tree? mid) (to-tree suf)
   :else (create-deep-tree (ftp/-peekl mid)
                           (ftm/create-delayed-tree
                            (ftp/-popl mid)
                            (ftp/-measured-popl mid))
                           suf)))

(defn- deep-right
  "Creates deep finger tree from pre mid suf,
  where seq (and mid) is possibly an empty tree."
  [pre mid suf]
  (cond
   (not (ftp/-empty-tree? suf)) (create-deep-tree pre mid suf)
   (ftp/-empty-tree? mid) (to-tree pre)
   :else (create-deep-tree pre
                           (ftm/create-delayed-tree
                            (ftp/-popr mid)
                            (ftp/-measured-popr mid))
                           (ftp/-peekr mid))))

(defn- nodes
  "Returns vector of digits created from sequence of objects."
  [xs]
  (let [v (vec xs), c (count v)]
    (seq
     (loop [i 0, nds []]
       (condp == (- c i)
           2 (conj nds (create-digit (v i) (v (+ 1 i))))
           3 (conj nds (create-digit (v i) (v (+ 1 i)) (v (+ 2 i))))
           4 (-> nds
                 (conj (create-digit (v i) (v (+ 1 i))))
                 (conj (create-digit (v (+ 2 i)) (v (+ 3 i)))))
           (recur (+ 3 i)
                  (conj nds (create-digit (v i)
                                          (v (+ 1 i))
                                          (v (+ 2 i))))))))))

(deftype DeepTree [pre mid suf measure-ref]
  IPrintable
  (-pr-seq [_ _2] (list "#<DFT[" (pr-str @measure-ref)
                        "] " (pr-str pre) ", " (pr-str mid)
                        ", " (pr-str suf) ">"))
  ftp/IEmptyTree
  (-empty-tree? [_] false)
  ftp/IDeque
  (-conjl [_ o] (if (< (count pre) 4)
                  (create-deep-tree (ftp/-conjl pre o) mid suf)
                  (let [[o1 o2 o3 o4] (seq pre)
                        new-digit (create-digit o2 o3 o4)]
                    (create-deep-tree (create-digit o o1)
                                      (ftp/-conjl mid new-digit)
                                      suf))))
  (-conjr [_ o] (if (< (count suf) 4)
                  (create-deep-tree pre mid (ftp/-conjr suf o))
                  (let [[o1 o2 o3 o4] (seq suf)
                        new-digit (create-digit o1 o2 o3)]
                    (create-deep-tree pre
                                      (ftp/-conjr mid new-digit)
                                      (create-digit o4 o)))))
  (-peekl [_] (ftp/-peekl pre))
  (-peekr [_] (ftp/-peekr suf))
  (-popl [_] (deep-left (ftp/-popl pre) mid suf))
  (-popr [_] (deep-right pre mid (ftp/-popr suf)))
  ftp/IMeasured
  (-measured [_] @measure-ref)
  ftp/IMeasuredTree
  (-measured-popl [_] (when measure/-combine-measures-fn
                        (measure/-combine-measures-fn
                         (measure/-combine-measures-fn
                          (ftp/-measured-popl pre)
                          (ftp/-measured mid))
                         (ftp/-measured suf))))
  (-measured-popr [_] (when measure/-combine-measures-fn
                        (measure/-combine-measures-fn
                         (measure/-combine-measures-fn
                          (ftp/-measured pre)
                          (ftp/-measured mid))
                         (ftp/-measured-popr suf))))
  ftp/ISplittableTree
  (-split [_ pred acc]
    (let [vpr (measure/-combine-measures-fn acc (ftp/-measured pre))]
      (if (pred vpr)
        ;; split in pred
        (let [[sl sx sr] (ftp/-split pre pred acc)]
          [(to-tree sl)
           sx
           (deep-left sr mid suf)])
        (let [vm
              (measure/-combine-measures-fn vpr (ftp/-measured mid))]
          (if (pred vm)
            ;; split in mid
            (let [[ml xs mr] (ftp/-split mid pred vpr)
                  [sl sx sr] (ftp/-split xs pred
                                         (measure/-combine-measures-fn
                                          vpr (ftp/-measured ml)))]
              [(deep-right pre ml sl) sx (deep-left sr mr suf)])
            ;; split in suf
            (let [[sl sx sr] (ftp/-split suf pred vm)]
              [(deep-right pre mid sl)
               sx
               (to-tree sr)]))))))
  ftp/IConcatenableTree
  (-app3 [t1 ts t2] (ftp/-app3deep t2 ts t1))
  (-app3deep [_ ts t1] (create-deep-tree
                        (.-pre t1)
                        (ftp/-app3 (.-mid t1)
                               (nodes
                                (concat (seq (.-suf t1))
                                        ts
                                        (seq pre)))
                               mid)
                        suf)))

(defn create-deep-tree
  "Creates new deep tree.
  Returns created tree."
  ([pre mid suf]
     (create-deep-tree pre mid suf
                       (if measure/-combine-measures-fn
                         (delay (measure/-combine-measures-fn
                                 (measure/-combine-measures-fn
                                  (ftp/-measured pre)
                                  (ftp/-measured mid))
                                 (ftp/-measured suf)))
                         (delay nil))))
  ([pre mid suf measure-val]
     (DeepTree. pre mid suf measure-val)))

;;;; Public API

(def empty-tree (EmptyTree.))

(defn to-tree
  "Creates finger tree from x seq."
  [x]
  (if-let [xs (seq x)]
    (apply ftc/conjr empty-tree xs)
    empty-tree))

(defn create
  "Creates finger tree containing supplied elements."
  [& xs]
  (to-tree xs))
