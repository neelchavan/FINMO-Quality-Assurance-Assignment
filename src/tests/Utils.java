package tests;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;

public class Utils {
	private static String uname = "AK_FINMO_SBX_A2D78B762B7B4354BC778153FFD71986";
	private static String pass = "SK_FINMO_SBX_F616A2FE_856E_4251_A058_FFE912CEA604";
	
	public static String getUname() {
		return uname;
	}
	
	public static String getPass() {
		return pass;
	}
	
	public static String createPayin(String amount, String description) {
		
		RestAssured.baseURI = "https://api.qafinmo.net";
		String response = given().auth().preemptive().basic(uname, pass).header("Content-Type", "application/json")
				.body("{\r\n" + "  \"amount\": "+Integer.parseInt(amount)+",\r\n" + "  \"currency\": \"AUD\",\r\n"
						+ "  \"payin_method_name\": \"au_bank_npp\",\r\n" + "  \"description\":\""+description+"\"\r\n" + "}")
				.when().post("/v1/payin").then().assertThat().statusCode(201).extract().response().asString();
		System.out.println("PASS: testCase01/createPayin");
		return response;
	}
}
