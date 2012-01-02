;;
;; Compute the factorial of a given number

(defun fact (n) 
  (if (< n 2) 
      1 
    (* n (fact (- n 1)))
    )
  )

;; compute the factorial for 1 to 10
(setq i 1)
(while (< i 10)
  (println (fact i))
  (setq i (+ i 1))
  )
