package ncreep.stream

import scala.language.experimental.modularity
import ncreep.streaming_purified.Monoid

enum Stream[+A]:
  case Map[A, B](stream: Stream[A], f: A => B) extends Stream[B]
  case Foreach[A](stream: Stream[A], f: A => Unit) extends Stream[Unit]
  case Grouped(stream: Stream[A], size: Int) extends Stream[List[A]]
  case FromList(ls: List[A])

  def map[B](f: A => B): Stream[B] = Map(this, f)

  def foreach(f: A => Unit): Stream[Unit] = Foreach(this, f)

  def grouped(size: Int): Stream[List[A]] = Grouped(this, size)

  def to[B, A1 >: A](sink: Sink[A1, B]): B = sink.run(this)

object Stream:
  def apply[A](xs: A*): Stream[A] = Stream.FromList(xs.toList)

trait Sink[-In, +Out]:
  self =>
  def run(stream: Stream[In]): Out

  def map[Out2](f: Out => Out2): Sink[In, Out2] = stream =>
    f(self.run(stream))

object Sink:
  // we are iterating over the stream only for the side-effects
  def discard[A]: Sink[A, Unit] = stream => toIterator(stream).foreach(_ => ())

  def fold[A, B](empty: B)(combine: (B, A) => B): Sink[A, B] =
    stream => toIterator(stream).foldLeft(empty)(combine)

  def foldMonoid[A: Monoid]: Sink[A, A] = fold(A.empty)(_ combine _)

  def toList[A]: Sink[A, List[A]] =
    // prepending and then reversing so as to not go quadratic on repeated `List` append calls
    fold[A, List[A]](List.empty)(_.prepended(_))
      .map(_.reverse)

  private def toIterator[A](stream: Stream[A]): Iterator[A] = stream match
    case Stream.Map(stream, f) => toIterator(stream).map(f)
    case Stream.Foreach(stream, f) =>
      // using `map` (rather than `foreach`) here as we want to preserve
      // the number of elements in the "stream"
      toIterator(stream).map(f)
    case Stream.Grouped(stream, size) =>
      toIterator(stream).grouped(size).map(_.toList)
    case Stream.FromList(ls) => ls.iterator
