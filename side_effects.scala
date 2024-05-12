package ncreep.side_effects

import ncreep.types.*

class UberService(fetcher: Fetcher, enricher: Enricher, bookkeeper: Bookkeeper, storage: Storage):

  def fetchAndStore(user: UserID): Unit =
    val data = fetcher.fetch(user)
    val enriched = enricher.enrich(user, data)

    bookkeeper.bookkeep(data, enriched)
    storage.store(enriched)

@main def runUberService =
  val fetcher = new Fetcher.Default
  val enricher = new Enricher.Default
  val bookkeeper = new Bookkeeper.Default
  val storage = new Storage.Default

  val service = new UberService(fetcher, enricher, bookkeeper, storage)

  // Do stuff with the service
  ???

end runUberService

trait Fetcher:
  def fetch(user: UserID): UserData

object Fetcher:
  class Default extends Fetcher:
    def fetch(user: UserID): UserData = ???

trait Enricher:
  def enrich(user: UserID, data: UserData): EnrichedUserData

object Enricher:
  class Default extends Enricher:
    def enrich(user: UserID, data: UserData): EnrichedUserData = ???

trait Bookkeeper:
  def bookkeep(original: UserData, enriched: EnrichedUserData): Unit

object Bookkeeper:
  class Default extends Bookkeeper:
    def bookkeep(original: UserData, enriched: EnrichedUserData): Unit = ???

trait Storage:
  def store(data: EnrichedUserData): Unit

object Storage:
  class Default extends Storage:
    def store(data: EnrichedUserData): Unit = ???
