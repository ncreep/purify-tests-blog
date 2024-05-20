package ncreep.declarative

import ncreep.types.*

class UberService[Bookkept, Stored](
    fetcher: Fetcher,
    enricher: Enricher,
    bookkeeper: Bookkeeper[Bookkept],
    storage: Storage[Bookkept, Stored]):

  def fetchAndStore(user: UserID): Stored =
    val data = fetcher.fetch(user)
    val enriched = enricher.enrich(user, data)

    val bookkept = bookkeeper.bookkeep(data, enriched)
    val stored = storage.store(bookkept, enriched)

    stored

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

trait Bookkeeper[A]:
  def bookkeep(original: UserData, enriched: EnrichedUserData): A

object Bookkeeper:
  class Default extends Bookkeeper[Unit]:
    def bookkeep(original: UserData, enriched: EnrichedUserData): Unit = ???

trait Storage[Precondition, A]:
  def store(precondition: Precondition, data: EnrichedUserData): A

object Storage:
  class Default extends Storage[Unit, Unit]:
    def store(precondition: Unit, data: EnrichedUserData): Unit = ???
