package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import io.restassured.path.json.JsonPath;

public class GetPayinApi {
  String username = Utils.getUname();
  String password = Utils.getPass();

  // Tests

  // 1. create payin -> retreive payin_id -> hit getpayin api -> verify json
  // response.
  // verify on giving the correct payin id user should get the correct payment
  // details
  @Test(description = "getting payin via id", priority = 1)
  public void verifyGetPayinApi() {
    RestAssured.baseURI = "https://api.qafinmo.net";
    String response = Utils.createPayin("499", "hiking");
    // parsing the json response
    JsonPath js = new JsonPath(response);
    String id = js.getString("data.payin_id"); // payin_id retreived.
    given().auth().preemptive().basic(username, password).when().get("/v1/payin/" + id + "").then().assertThat()
        .statusCode(200);
    System.out.println("PASS: testCase01/getPayin");
  }

  // 2. verify the response payload for get payin api
  @Test(description = "verify the response payload for get payin api", priority = 2)
  public void verifyResponsePayload() {
    RestAssured.baseURI = "https://api.qafinmo.net";
    String response = Utils.createPayin("5999", "lunch");
    // parsing the json response
    JsonPath js = new JsonPath(response);
    String idActual = js.getString("data.payin_id"); // payin_id retreived.
    String amountActual = js.getString("data.amount"); // amount retreived.
    String currencyActual = js.getString("data.currency");// currency retreived.
    String res = given().auth().preemptive().basic(username, password).when().get("/v1/payin/" + idActual + "").then()
        .assertThat().statusCode(200).extract().response().asString();
    JsonPath jsGet = new JsonPath(res);
    String idExpected = jsGet.getString("data.payin_id");
    String amountExpected = jsGet.getString("data.amount");
    String currencyExpected = jsGet.getString("data.currency");
    // verify if the CreatePayinApi payload and GetPayinApi response matches
    Assert.assertEquals(idActual, idExpected);
    Assert.assertEquals(amountActual, amountExpected);
    Assert.assertEquals(currencyActual, currencyExpected);
    System.out.println("PASS: testCase02/getPayin");
  }

  // 3. verify if the api can handle incorrect get request
  @Test(description = "verify if the api can handle incorrect get request", priority = 3)
  public void verifyInvalidGetRequest() {
    RestAssured.baseURI = "https://api.qafinmo.net";
    String invalidId = "payin_2b8a6d481b8349928845ec607a15e458ss";
    given().auth().preemptive().basic(username, password).when().get("/v1/payin/" + invalidId + "").then().assertThat()
        .statusCode(404);
    System.out.println("PASS: testCase03/getPayin");
  }

  // 4. verify if the api should not complete the get request without
  // authorization
  @Test(description = "verify if the api should not complete the get request without authorization", priority = 4)
  public void verifyGetApiWithoutAuth() {
    RestAssured.baseURI = "https://api.qafinmo.net";
    String payin_id = "payin_2b8a6d481b8349928845ec607a15e458";
    given().when().get("/v1/payin/" + payin_id + "").then().assertThat().statusCode(401);
    System.out.println("PASS: testCase04/getPayin");
  }
}
