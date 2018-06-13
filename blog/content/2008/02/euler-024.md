---
layout: post
title: "Euler 024"
date: 2008-02-03
---

[Problem 024]\:

```scala
object Euler24 extends Application {
  def fact (n: Int): Int = if (n == 0) 1 else n * fact(n - 1)
  def nthperm (target :Int, nums :List[Int]) :String = {
    if (nums.length == 1) return nums(0).toString;
    val nfact = fact(nums.length-1)
    val idx = (target / nfact).intValue;
    val digit = nums(idx);
    return digit + nthperm(target - nfact*idx, nums.filter(digit.!=));
  }
  println(nthperm(1000000, List.range(0, 10)));
}
```
We know there are 362880 (9!) permutations starting with 0 and another 9! starting with 1. That leaves only 274240 before we get to the millionth, so we know the first digit of the desired permutation is 2. Algorithmically, we just divide (n-1)! into the target value to determine which digit from our list of remaining digits to take. To start we have 1000000 / 9! which is 2 remainder 274240. Since we have all of our digits at the start, we take the 2nd (counting from zero like good computer scientists) which is 2. Then we divide 8! into 274240 and we get 6. We've already used the 2 so the 6th digit in our list is now 7. So we take that and then keep going until we've used up all our digits.



[Problem 024]: http://projecteuler.net/index.php?section=problems&id=24
