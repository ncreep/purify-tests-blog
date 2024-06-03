package ncreep.parameter_inputs

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UberServiceTest extends AnyWordSpec with Matchers:
  "The uber-service" should:
    "fetch the user data, enrich it, and store the results" in:
      val service = new UberService(TestFetcher, TestEnricher, TestBookkeeper, TestStorage)

      val result = service.fetchAndStore(5)

      result shouldBe List(
        "bookkept: data: 5",
        "bookkept: enriched: 5 - data: 5",
        "stored: enriched: 5 - data: 5"
      )

  type UserID = Int
  type UserData = String
  type EnrichedUserData = String

  object TestFetcher extends Fetcher[UserID, UserData]:
    def fetch(user: UserID): UserData = s"data: $user"

  object TestEnricher extends Enricher[UserID, UserData, EnrichedUserData]:
    def enrich(user: UserID, data: UserData): EnrichedUserData =
      s"enriched: $user - $data"

  object TestBookkeeper extends Bookkeeper[List[String], UserData, EnrichedUserData]:

    def bookkeep(original: UserData, enriched: EnrichedUserData): List[String] =
      List(s"bookkept: $original", s"bookkept: $enriched")

  object TestStorage extends Storage[List[String], List[String], EnrichedUserData]:

    def store(bookkeepingResult: List[String], data: EnrichedUserData): List[String] =
      bookkeepingResult :+ s"stored: $data"
