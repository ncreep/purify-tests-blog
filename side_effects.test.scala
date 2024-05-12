package ncreep.side_effects

import ncreep.types.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.mutable.ListBuffer

class UberServiceTest extends AnyWordSpec with Matchers:

  "The uber-service" should: 
    "fetch the user data, enrich it, and store the results" in:
      val bookkeeper = new TestBookkeeper
      val storage = new TestStorage

      val service = new UberService(TestFetcher, TestEnricher, bookkeeper, storage)

      val expectedUserData = UserData(s"data: 5")
      val expectedEnriched = EnrichedUserData("enriched: 5 - data: 5")

      service.fetchAndStore(UserID(5))

      bookkeeper.bookkept shouldBe List((expectedUserData, expectedEnriched))
      storage.stored shouldBe List(expectedEnriched)

  object TestFetcher extends Fetcher:
    def fetch(user: UserID): UserData = UserData(s"data: ${user.value}")

  object TestEnricher extends Enricher:
    def enrich(user: UserID, data: UserData): EnrichedUserData =
      EnrichedUserData(s"enriched: ${user.value} - ${data.value}")

  class TestBookkeeper extends Bookkeeper:
    var bookkept: List[(UserData, EnrichedUserData)] = List.empty

    def bookkeep(original: UserData, enriched: EnrichedUserData): Unit =
      bookkept ::= ((original, enriched))

  class TestStorage extends Storage:
    var stored: List[EnrichedUserData] = List.empty

    def store(data: EnrichedUserData): Unit =
      stored ::= data
