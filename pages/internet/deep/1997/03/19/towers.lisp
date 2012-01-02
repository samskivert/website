;;;======================================================
;;; Simple towers of Hanoi program. Note that Start-Peg
;;; and Goal-Peg are integers from 1 to 3 indicating the
;;; peg number. Ie to move 4 discs, starting on the first
;;; peg and finishing on the last one, execute (Towers 4
;;; 1 3)
;;;
;;; 1992 Marty Hall. hall@aplcenmp.apl.jhu.edu
;;;
;;; Slightly modified to work with Uncommon Lisp
;;;
;;; 1997 Michael Bayne. mdb@go2net.com

(defun remaining-peg (peg1 peg2) (- 6 peg1 peg2))

(defun towersh (number-of-discs start-peg goal-peg)
  (towers (- number-of-discs 1)
          start-peg
          (remaining-peg start-peg goal-peg))
  (towers 1 start-peg goal-peg)
  (towers (- number-of-discs 1)
          (remaining-peg start-peg goal-peg)
          goal-peg)
  )

(defun towers (number-of-discs start-peg goal-peg)
  (if (= 1 number-of-discs) 
      (println "move top disc from peg " start-peg 
               " to peg " goal-peg ".")
    (towersh number-of-discs start-peg goal-peg)
    )
  )

;(towers 2 1 2)
;(towers 3 1 2)
(towers 4 1 2)
