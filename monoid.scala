package ncreep.streaming_purified

import scala.language.experimental.modularity

trait Monoid:
  type Self

  def empty: Self

  extension (x: Self) infix def combine(y: Self): Self

object Monoid:
  def foldMap[A, B: Monoid](as: List[A])(f: A => B) =
    as.map(f).foldLeft(B.empty)(_ combine _)

  given Unit is Monoid:
    def empty = ()
    extension (x: Unit) infix def combine(y: Unit): Unit = ()

  given [A] => List[A] is Monoid:
    def empty = List.empty
    extension (x: List[A]) infix def combine(y: List[A]): List[A] = x ++ y
