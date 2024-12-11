package ncreep.streaming

import ncreep.stream.*
import ncreep.types.*

class UberJob(fetcher: Fetcher, enricher: Enricher, storage: Storage):

  def fetchAndStore: Unit =
    fetcher
      .fetch
      .map(enricher.enrich)
      .grouped(5)
      .foreach(storage.storeBatch)
      .to(Sink.discard)

trait Fetcher:
  def fetch: Stream[UserData]

object Fetcher:
  class Default extends Fetcher:
    def fetch: Stream[UserData] = ???

trait Enricher:
  def enrich(data: UserData): EnrichedUserData

object Enricher:
  class Default extends Enricher:
    def enrich(data: UserData): EnrichedUserData = ???

trait Storage:
  def storeBatch(data: List[EnrichedUserData]): Unit

object Storage:
  class Default extends Storage:
    def storeBatch(data: List[EnrichedUserData]): Unit = ???
