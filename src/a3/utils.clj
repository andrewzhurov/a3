(ns a3.utils)

(defmacro l [expr]
  (list 'do
        (list println (pr-str expr) ":" (list pr-str expr))
        expr))
