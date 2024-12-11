package ncreep.streaming_purified

import scala.language.experimental.modularity
import ncreep.stream.*
import ncreep.types.*

case class EnrichedUserData(userId: String, registered: Boolean, friends: List[String])

case class Stats(count: Int, registered: Int, friends: Int):
  def averageFriends = friends.toDouble / count

  override def toString: String =
    productElementNames.zip(productIterator)
      .map((name, value) => s"$name = $value")
      .toList
      .appended(s"avgFriends = ${averageFriends}")
      .mkString(s"$productPrefix(", ", ", ")")

object Stats:
  def fromUserData(data: EnrichedUserData): Stats =
    Stats(
      count = 1,
      registered = if data.registered then 1 else 0,
      friends = data.friends.length)

  def fromBatch(data: List[EnrichedUserData]): Stats =
    Monoid.foldMap(data)(fromUserData)

  given Stats is Monoid:
    def empty = Stats(0, 0, 0)

    extension (x: Stats) infix def combine(y: Stats): Stats =
      Stats(x.count + y.count, x.registered + y.registered, x.friends + y.friends)

object DataForStreams:
  val users =
    Stream(
      UserData("a"),
      UserData("b"),
      UserData("c"),
      UserData("d"),
      UserData("e"),
      UserData("f"),
      UserData("g"),
      UserData("h"),
      UserData("i"),
      UserData("j"),
      UserData("k"))

  val userData = Map(
    "a" -> EnrichedUserData(userId = "a", registered = true, friends = List("b", "c")),
    "b" -> EnrichedUserData(
      userId = "b",
      registered = false,
      friends = List("b", "c", "d", "e", "f")),
    "c" -> EnrichedUserData(userId = "c", registered = true, friends = List("a")),
    "d" -> EnrichedUserData(
      userId = "d",
      registered = true,
      friends = List("a", "f", "k")),
    "e" -> EnrichedUserData(userId = "e", registered = false, friends = List("a", "e", "f")),
    "f" -> EnrichedUserData(userId = "f", registered = true, friends = List("j", "k")),
    "g" -> EnrichedUserData(
      userId = "g",
      registered = false,
      friends = List("i", "j", "c")),
    "h" -> EnrichedUserData(
      userId = "h",
      registered = false,
      friends = List("b", "d", "a", "c")),
    "i" -> EnrichedUserData(
      userId = "i",
      registered = false,
      friends = List("j", "i", "a", "c", "e")),
    "j" -> EnrichedUserData(userId = "j", registered = true, friends = List("i", "e", "d")),
    "k" -> EnrichedUserData(userId = "k", registered = true, friends = List.empty))
