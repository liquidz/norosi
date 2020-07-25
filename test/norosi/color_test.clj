(ns norosi.color-test
  (:require
   [clojure.test :as t]
   [norosi.color :as sut]))

(t/deftest parse-color-code-test
  (t/are [expected in] (= expected (sut/parse-color-code in))
    [31] "31"
    [170 187 204] "abc"
    [170 187 204] "aabbcc"
    [170 187 204] "ABC"
    [170 187 204] "AABBCC"
    [81 168 221] "51A8DD"
    [] "invalid"))
