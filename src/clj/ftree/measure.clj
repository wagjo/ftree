;; Copyright (C) 2012, Jozef Wagner. All rights reserved.

(ns ftree.measure
  "Definition for with-measure macro.")

;;;; Public API

(defmacro with-measure
  "Binds given measure."
  [m & body]
  `(let [m# ~m] ; to compute m only once
     (binding [ftree.measure/-empty-tree-measure
               (:empty-tree-measure m#)
               ftree.measure/-combine-measures-fn
               (:combine-measures-fn m#)
               ftree.measure/-measure-element-fn
               (:measure-element-fn m#)]
       ~@body)))

;;;; Comments

(comment

  (macroexpand-1 '(with-measure {1 2 3 4} b1 b2 b3))
  
  )
