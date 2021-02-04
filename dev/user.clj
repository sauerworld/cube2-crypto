(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.pprint :refer (pprint)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(def test-key "f373de2d49584e7a16166e76b1bb925f24f0130c63ac9332")


;; Notes:
;; Cube2 auth process:
;; generate-challenge
;; - random 192 bit message created
;; - challenge is messsage multiplied by the curve base point, normalized, checked for positivity, hex
;; - answer is the message multiplied by the pubkey (turned into point), normalized, unchecked, hex
;;
;; generate-answer
;; - turn the challenge into point
;; - multiply by privkey
;; - hex str, unchecked
;;
;; Methods:
;; - Positivity check:
;;   An EC Point is positive if Y is even
;;
;; - How cube2 turns int into ec point
;;   - int checked for negativity, then abs, becomes x
;;   - y is x multipled by a bunch of stuff, added to B (ecc param), sqrt, multiplied by -1 if int was neg
;;   - z is 1


(require '[sauerworld.cube2.crypto :refer :all]
         '[criterium.core :refer [quick-bench]])

(def priv "230576d3e6ebf6284328f764056dca1ed8dc1ba5021e34ee")
(def pub (get-pubkey priv))

(def chal (:challenge (challenge pub)))


;;;; Benchmarks (Bouncy castle 1.51)

;; (quick-bench (get-pubkey priv))
;; Evaluation count : 450 in 6 samples of 75 calls.
;;              Execution time mean : 1.389484 ms
;;     Execution time std-deviation : 20.304669 µs
;;    Execution time lower quantile : 1.355326 ms ( 2.5%)
;;    Execution time upper quantile : 1.408540 ms (97.5%)
;;                    Overhead used : 9.153514 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; 	low-severe	 1 (16.6667 %)
;;  Variance from outliers : 13.8889 % Variance is moderately inflated by outliers


;; (quick-bench (challenge pub))
;; Evaluation count : 204 in 6 samples of 34 calls.
;;              Execution time mean : 3.082849 ms
;;     Execution time std-deviation : 223.721540 µs
;;    Execution time lower quantile : 2.958022 ms ( 2.5%)
;;    Execution time upper quantile : 3.466376 ms (97.5%)
;;                    Overhead used : 9.153514 ns

;; Found 2 outliers in 6 samples (33.3333 %)
;; 	low-severe	 1 (16.6667 %)
;; 	low-mild	 1 (16.6667 %)
;;  Variance from outliers : 15.2931 % Variance is moderately inflated by outliers

;; (quick-bench (answer priv chal))
;; Evaluation count : 342 in 6 samples of 57 calls.
;;              Execution time mean : 1.825257 ms
;;     Execution time std-deviation : 202.381291 µs
;;    Execution time lower quantile : 1.654390 ms ( 2.5%)
;;    Execution time upper quantile : 2.159018 ms (97.5%)
;;                    Overhead used : 9.153514 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; 	low-severe	 1 (16.6667 %)
;;  Variance from outliers : 30.9797 % Variance is moderately inflated by outliers


;;;; Benchmarks (Bouncy Castle 1.58)

;; (quick-bench (get-pubkey priv))
;; Evaluation count : 444 in 6 samples of 74 calls.
;;              Execution time mean : 1.464123 ms
;;     Execution time std-deviation : 206.189692 µs
;;    Execution time lower quantile : 1.348904 ms ( 2.5%)
;;    Execution time upper quantile : 1.815114 ms (97.5%)
;;                    Overhead used : 9.126470 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; 	low-severe	 1 (16.6667 %)
;;  Variance from outliers : 31.8744 % Variance is moderately inflated by outliers

;; (quick-bench (challenge pub))
;; Evaluation count : 198 in 6 samples of 33 calls.
;;              Execution time mean : 3.001078 ms
;;     Execution time std-deviation : 38.588372 µs
;;    Execution time lower quantile : 2.952812 ms ( 2.5%)
;;    Execution time upper quantile : 3.046606 ms (97.5%)
;;                    Overhead used : 9.126470 ns

;; (quick-bench (answer priv chal))
;; Evaluation count : 390 in 6 samples of 65 calls.
;;              Execution time mean : 1.574272 ms
;;     Execution time std-deviation : 11.264550 µs
;;    Execution time lower quantile : 1.560600 ms ( 2.5%)
;;    Execution time upper quantile : 1.586169 ms (97.5%)
;;                    Overhead used : 9.126470 ns
