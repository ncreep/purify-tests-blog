package ncreep.purified

import ncreep.types.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.mutable.ListBuffer

class InvocationOrderTest extends AnyWordSpec with Matchers:
  "The uber-service" should:
    "invoke bookkeeping before storage" in:
      val invocations = ListBuffer.empty[String]

      val bookkeeper = new TestBookkeeper(invocations)
      val storage = new TestStorage(invocations)

      val service = new UberService(TestFetcher, TestEnricher, bookkeeper, storage)

      val _ = service.fetchAndStore(UserID(5))

      invocations shouldBe List("bookkeep", "store")

  object TestFetcher extends Fetcher:
    def fetch(user: UserID): UserData = UserData(s"data: ${user.value}")

  object TestEnricher extends Enricher:
    def enrich(user: UserID, data: UserData): EnrichedUserData =
      EnrichedUserData(s"enriched: ${user.value} - ${data.value}")

  class TestBookkeeper(invocations: ListBuffer[String]) extends Bookkeeper[(UserData, EnrichedUserData)]:

    def bookkeep(original: UserData, enriched: EnrichedUserData): (UserData, EnrichedUserData) =
      invocations += "bookkeep"

      (original, enriched)

  class TestStorage(invocations: ListBuffer[String]) extends Storage[EnrichedUserData]:

    def store(data: EnrichedUserData): EnrichedUserData =
      invocations += "store"

      data
