package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

public class CreatePayinApi {
	static String username = Utils.getUname();
	static String password = Utils.getPass();

	// 1. verify if user can create payin
	@Test(description = "verify if user can create payin", priority = 1)
	public void createPayin() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		given().auth().preemptive().basic(username, password).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 2202,\r\n" + "  \"currency\": \"AUD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"party\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(201);
		System.out.println("PASS: testCase01/createPayin");
	}

	// 2. verify user should not be able to create payin without authorization
	@Test(description = "verify user should not be able to create payin without authorization", priority = 2)
	public void verifyPayinWithoutAuth() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		given().header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 100,\r\n" + "  \"currency\": \"AUD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"bill\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(401);
		System.out.println("PASS: testCase02/createPayin");
	}

	// 3. verify user should not be able to create payin with very large payment
	// amount
	@Test(description = "verify user should not be able to create payin with very large payment amount", priority = 3)
	public void verifyLargePayment() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		given().auth().preemptive().basic(username, password).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 35000000,\r\n" + "  \"currency\": \"AUD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"checkout\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(400);
		System.out.println("PASS: testCase03/createPayin");
	}

	// 4. verify user should not be able to create payin without filling all the
	// mandatory fields
	@Test(description = "verifying invalid inputs", priority = 4)
	public void verifyMandatoryFieldsOfApi() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		given().auth().preemptive().basic(username, password).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 35000000,\r\n" + "  \"currency\":\"\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"checkout\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(400);
		System.out.println("PASS: testCase04/createPayin");
	}

	// 5. verify the response body on creating the api
	@Test(description = "verify the response body on creating the api", priority = 5)
	public void verifyCreatePayinApiResBody() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		String response = given().auth().preemptive().basic(username, password).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 1708,\r\n" + "  \"currency\": \"AUD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"flight booking\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(201).extract().response().asString();
		// parsing string json response
		JsonPath js = new JsonPath(response);
		String amountExpected = js.getString("data.amount");
		String currencyExpected = js.getString("data.currency");
		String descExpected = js.getString("data.description");
		Assert.assertEquals("1708", amountExpected);
		Assert.assertEquals("AUD", currencyExpected);
		Assert.assertEquals("flight booking", descExpected);
		System.out.println("PASS: testCase05/createPayin");
	}

	// 6. verify if the api can handle non acceptable currency
	@Test(description = "verify if the api can handle non acceptable currency", priority = 6)
	public void verifyNonAcceptableCurrency() {
		RestAssured.baseURI = "https://api.qafinmo.net";
		given().auth().preemptive().basic(username, password).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": 1708,\r\n" + "  \"currency\": \"USD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\"flight booking\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(403);
		System.out.println("PASS: testCase06/createPayin");
	}
}
