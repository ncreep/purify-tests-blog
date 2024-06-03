package ncreep.parameter_inputs

import ncreep.types.*

class UberService[UserID, UserData, EnrichedUserData, Bookkept, Stored](
    fetcher: Fetcher[UserID, UserData],
    enricher: Enricher[UserID, UserData, EnrichedUserData],
    bookkeeper: Bookkeeper[Bookkept, UserData, EnrichedUserData],
    storage: Storage[Bookkept, Stored, EnrichedUserData]):

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

trait Fetcher[UserID, UserData]:
  def fetch(user: UserID): UserData

object Fetcher:
  class Default extends Fetcher[UserID, UserData]:
    def fetch(user: UserID): UserData = ???

trait Enricher[UserID, UserData, EnrichedUserData]:
  def enrich(user: UserID, data: UserData): EnrichedUserData

object Enricher:
  class Default extends Enricher[UserID, UserData, EnrichedUserData]:
    def enrich(user: UserID, data: UserData): EnrichedUserData = ???

trait Bookkeeper[A, UserData, EnrichedUserData]:
  def bookkeep(original: UserData, enriched: EnrichedUserData): A

object Bookkeeper:
  class Default extends Bookkeeper[Unit, UserData, EnrichedUserData]:
    def bookkeep(original: UserData, enriched: EnrichedUserData): Unit = ???

trait Storage[Precondition, A, EnrichedUserData]:
  def store(precondition: Precondition, data: EnrichedUserData): A

object Storage:
  class Default extends Storage[Unit, Unit, EnrichedUserData]:
    def store(precondition: Unit, data: EnrichedUserData): Unit = ???
