;; Copyright (c) Rich Hickey, Chris Houser. All rights reserved.
;; Copyright (C) 2012, Jozef Wagner. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file epl-v10.html at the root of this distribution.
;;
;; By using this software in any fashion, you are agreeing to be bound
;; by the terms of this license.
;;
;; You must not remove this notice, or any other, from this software.

(ns ftree.tree
  "Macros for ftree digit and delayed tree.")

;;;; Implementation details

(defn- digit-typename
  "Generates Digit type name based on number of items."
  [items]
  (symbol (str "Digit" (count items))))

(defn- digit-pop
  "Generates code for -popX."
  [items pop-fn]
  (if (> (count items) 1)
    `(ftree.tree/create-digit ~@(pop-fn items))
    `ftree.tree/empty-tree))

(defn- digit-measure
  "Generates code which measures each item and
  combines them together one by one."
  [items]
  (reduce #(list `measure/-combine-measures-fn %1 %2)
          (map #(list `ftp/-measured %) items)))

(defn- digit-measured-pop
  "Generates code for -measured-popX."
  [items pop-fn]
  (if (> (count items) 1)
    (digit-measure (pop-fn items))
    `measure/-empty-tree-measure))

(defn- digit-conj
  "Generates code for -conjX."
  [items o conj-left?]
  (if (< (count items) 4)
    (if conj-left?
      `(ftree.tree/create-digit ~o ~@items)
      `(ftree.tree/create-digit ~@items ~o))
    `(throw ex/unsupported-operation)))

(defn- digit-split-inner
  "Generates code for inner part of -split."
  [items pred-fn acc]
  (letfn [(step [ips [ix & ixs]]
            (if (empty? ixs)
              [(if ips
                 `(ftree.tree/create-digit ~@ips)
                 `ftree.tree/empty-tree)
               ix
               `ftree.tree/empty-tree]
              `(let [~acc (measure/-combine-measures-fn
                           ~acc
                           (ftp/-measured ~ix))]
                 (if (~pred-fn ~acc)
                   [~(if ips
                       `(ftree.tree/create-digit ~@ips)
                       `ftree.tree/empty-tree)
                    ~ix
                    (ftree.tree/create-digit ~@ixs)]
                   ~(step (concat ips [ix]) ixs)))))]
    (step nil items)))

(defn- digit-split-slow
  "Generates code for -split."
  [items pred-fn acc]
  `(if measure/-combine-measures-fn
     ~(digit-split-inner items pred-fn acc)
     (throw ex/unsupported-operation)))

(defn- digit-split
  "Generates code for -split."
  [items pred-fn acc]
  (digit-split-inner items pred-fn acc))

(defn- digit-print
  "Generates code for -pr-seq."
  [items]
  `(list "#<"
         ~(name (digit-typename items))
         "["
         (~'pr-str @~'measure-ref)
         "] "
         ~@(interpose ", "
                      (map #(list `pr-str %)
                           items))
         ">"))

(defn- digit-seq
  "Generates code for -seq."
  [items]
  (reduce #(list `cons %2 %1) nil (reverse items)))

;;;; Public API

(defmacro defdigit [& items]
  (let [o (gensym "o_")
        pred-fn (gensym "pred-fn_")
        acc (gensym "acc_")]
    `(deftype ~(digit-typename items) [~@items ~'measure-ref]
       ~'ISeqable
       (~'-seq [~'_] ~(digit-seq items)) ; needed for deep ops
       ~'ICounted
       (~'-count [~'_] ~(count items)) ; needed for deep ops
       ~'IPrintable
       (~'-pr-seq [~'_ ~'_2] ~(digit-print items))
       ftp/IEmptyTree
       (~'-empty-tree? [~'_] false)
       ftp/IDeque
       (~'-conjl [~'_ ~o] ~(digit-conj items o true))
       (~'-conjr [~'_ ~o] ~(digit-conj items o false))
       (~'-peekl [~'_] ~(first items))
       (~'-peekr [~'_] ~(last items))
       (~'-popl [~'_] ~(digit-pop items rest))
       (~'-popr [~'_] ~(digit-pop items drop-last))
       ftp/IMeasured
       (~'-measured [~'_] @~'measure-ref)
       ftp/IMeasuredTree
       (~'-measured-popl [~'_] ~(digit-measured-pop items rest))
       (~'-measured-popr [~'_] ~(digit-measured-pop items drop-last))
       ftp/ISplittableTree
       (~'-split [~'_ ~pred-fn ~acc]
         ~(digit-split items pred-fn acc)))))

(defmacro make-digit
  "Generates code which creates new object of correct Digit type."
  [& items]
  `(new ~(digit-typename items) ~@items
        (if measure/-combine-measures-fn
          (delay ~(digit-measure items))
          (delay nil))))

(defmacro create-delayed-tree
  "Generates code which creates delayed tree."
  [tree-expr measure-val]
  `(new ftree.tree/DelayedTree (delay ~tree-expr) ~measure-val))

;;;; Comments

(comment

  (digit-print [:a :b])

  (digit-seq [:a :b])

  (digit-split [:a :b :c :d] :pred :acc)
  
  (macroexpand-1 '(defdigit :a))
  (macroexpand-1 '(defdigit :a :b))
  (macroexpand-1 '(defdigit :a :b :c))
  (macroexpand-1 '(defdigit :a :b :c :d))
  
  (digit-measure '(a))
  (digit-measure '(a b))
  (digit-measure '(a b c))
  (digit-measure '(a b c d))

  (digit-typename '(a b c d))

  (macroexpand-1 '(make-digit a))

  (macroexpand-1 '(create-delayed-tree :t :m))

  )
