package ncreep.streaming_purified

import ncreep.stream.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UberJobTest extends AnyWordSpec with Matchers:
  "The uber-service" should:
    "fetch the user data, enrich it, and store the results" in:
      val service = UberJob(TestFetcher, TestEnricher, TestStorage)

      val result = service.fetchAndStore

      result shouldBe List(
        "stored: enriched: data1",
        "stored: enriched: data2",
        "stored: enriched: data3")

  type UserData = String
  type EnrichedUserData = String
  type Stored = List[String]

  object TestFetcher extends Fetcher[UserData]:
    def fetch: Stream[UserData] =
      Stream("data1", "data2", "data3")

  object TestEnricher extends Enricher[UserData, EnrichedUserData]:
    def enrich(data: UserData): EnrichedUserData =
      s"enriched: $data"

  object TestStorage extends Storage[EnrichedUserData, Stored]:

    def storeBatch(data: List[EnrichedUserData]): Stored =
      data.map("stored: " + _)
