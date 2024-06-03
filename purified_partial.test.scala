package ncreep.purified_partial

import ncreep.types.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.collection.mutable.ListBuffer

class UberServiceTest extends AnyWordSpec with Matchers:
  "The uber-service" should: 
    "fetch the user data, enrich it, and store the results" in: 
      val storage = new TestStorage

      val service = new UberService(TestFetcher, TestEnricher, TestBookkeeper, storage)

      val expectedUserData = UserData("data: 5")
      val expectedEnriched = EnrichedUserData("enriched: 5 - data: 5")

      val bookkeeperResult = service.fetchAndStore(UserID(5))

      bookkeeperResult shouldBe (expectedUserData, expectedEnriched)
      storage.stored shouldBe List(expectedEnriched)

  object TestFetcher extends Fetcher:
    def fetch(user: UserID): UserData = UserData(s"data: ${user.value}")

  object TestEnricher extends Enricher:
    def enrich(user: UserID, data: UserData): EnrichedUserData =
      EnrichedUserData(s"enriched: ${user.value} - ${data.value}")

  object TestBookkeeper extends Bookkeeper[(UserData, EnrichedUserData)]:

    def bookkeep(original: UserData, enriched: EnrichedUserData): (UserData, EnrichedUserData) =
      (original, enriched)

  class TestStorage extends Storage:
    val stored: ListBuffer[EnrichedUserData] = ListBuffer.empty

    def store(data: EnrichedUserData): Unit =
      stored += data
