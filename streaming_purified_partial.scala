package ncreep.streaming_purified_partial

import ncreep.types.*
import ncreep.stream.*

class UberJob[UserData, EnrichedUserData, Stored](
    fetcher: Fetcher[UserData],
    enricher: Enricher[UserData, EnrichedUserData],
    storage: Storage[Stored, EnrichedUserData]):

  def fetchAndStore: Unit =
    fetcher
      .fetch
      .map(enricher.enrich)
      .grouped(5)
      .map(storage.storeBatch)
      .to(Sink.discard)

trait Fetcher[UserData]:
  def fetch: Stream[UserData]

object Fetcher:
  class Default extends Fetcher[UserData]:
    def fetch: Stream[UserData] = ???

trait Enricher[UserData, EnrichedUserData]:
  def enrich(data: UserData): EnrichedUserData

object Enricher:
  class Default extends Enricher[UserData, EnrichedUserData]:
    def enrich(data: UserData): EnrichedUserData = ???

trait Storage[Stored, EnrichedUserData]:
  def storeBatch(data: List[EnrichedUserData]): Stored

object Storage:
  class Default extends Storage[Unit, EnrichedUserData]:
    def storeBatch(data: List[EnrichedUserData]): Unit = ???
