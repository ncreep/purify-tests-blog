package ncreep.streaming_purified

import ncreep.types.{EnrichedUserData => _, *}
import ncreep.stream.*
import scala.language.experimental.modularity

class UberJob[UserData, EnrichedUserData, Stored: Monoid](
    fetcher: Fetcher[UserData],
    enricher: Enricher[UserData, EnrichedUserData],
    storage: Storage[EnrichedUserData, Stored]):

  def fetchAndStore: Stored =
    fetcher
      .fetch
      .map(enricher.enrich)
      .grouped(5)
      .map(storage.storeBatch)
      .to(summarize)

  def summarize: Sink[Stored, Stored] = Sink.foldMonoid[Stored]

@main def runUberJob =
  val service = UberJob(Fetcher.Default, Enricher.Default, Storage.Default)

  service.fetchAndStore

  println("---- Done ----")

end runUberJob

@main def runUberJobStats =
  val service = UberJob(Fetcher.Default, Enricher.Default, Storage.WithStats)

  val stats = service.fetchAndStore

  println("---- Done ----")
  println(stats)

end runUberJobStats

trait Fetcher[UserData]:
  def fetch: Stream[UserData]

object Fetcher:
  object Default extends Fetcher[UserData]:
    def fetch: Stream[UserData] = DataForStreams.users

trait Enricher[UserData, EnrichedUserData]:
  def enrich(data: UserData): EnrichedUserData

object Enricher:
  object Default extends Enricher[UserData, EnrichedUserData]:

    def enrich(data: UserData): EnrichedUserData =
      DataForStreams.userData.getOrElse(data.value, sys.error(s"Unexpected user ${data.value}"))

trait Storage[EnrichedUserData, Stored]:
  def storeBatch(data: List[EnrichedUserData]): Stored

object Storage:
  object Default extends Storage[EnrichedUserData, Unit]:
    def storeBatch(data: List[EnrichedUserData]): Unit =
      println(s"Storing: [${data.map(_.userId).mkString(", ")}]")

  object WithStats extends Storage[EnrichedUserData, Stats]:
    def storeBatch(data: List[EnrichedUserData]): Stats =
      println(s"Storing: [${data.map(_.userId).mkString(", ")}]")

      Stats.fromBatch(data)
