package ncreep.purified

import ncreep.types.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UberServiceTest extends AnyWordSpec with Matchers:
  "The uber-service" should: 
    "fetch the user data, enrich it, and store the results" in: 
      val service = new UberService(TestFetcher, TestEnricher, TestBookkeeper, TestStorage)

      val expectedUserData = UserData(s"data: 5")
      val expectedEnriched = EnrichedUserData("enriched: 5 - data: 5")

      val (bookkeeperResult, storageResult) = service.fetchAndStore(UserID(5))

      bookkeeperResult shouldBe (expectedUserData, expectedEnriched)
      storageResult shouldBe expectedEnriched

  object TestFetcher extends Fetcher:
    def fetch(user: UserID): UserData = UserData(s"data: ${user.value}")

  object TestEnricher extends Enricher:
    def enrich(user: UserID, data: UserData): EnrichedUserData =
      EnrichedUserData(s"enriched: ${user.value} - ${data.value}")

  object TestBookkeeper extends Bookkeeper[(UserData, EnrichedUserData)]:

    def bookkeep(original: UserData, enriched: EnrichedUserData): (UserData, EnrichedUserData) =
      (original, enriched)

  object TestStorage extends Storage[EnrichedUserData]:

    def store(data: EnrichedUserData): EnrichedUserData = data
