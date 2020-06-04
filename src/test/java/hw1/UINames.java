package hw1;

import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


public class UINames {



    @BeforeAll
    public static void beforeAll(){
        baseURI = "http://cybertek-ui-names.herokuapp.com/api/";

    }

    /**
     * 1.Send a get request without providing any parameters
     * 2.Verify status code 200, content type application/json; charset=utf-83.
     * Verify that name, surname, gender, region fields have value
     */
    @Test
    @DisplayName("No params test")
    public void noParams(){
        Response response = get().prettyPeek();
        response.
                then().
                assertThat().
                statusCode(200).and().
                contentType("application/json; charset=utf-8").
                and().body("name", notNullValue()).
                body("surname", notNullValue()).
                body("region", notNullValue());
    }


    /**
     * Gender test
     * 1. create a request by providing query parameter: gender, male or female
     * 2. verify status code 200, content type application/json; charset=utf-8
     * 3. verify that value of gender field is same from step 1
     */

    @Test
    @DisplayName("Gender test")
    public void genderTest(){
        Collections.shuffle(genders);
        String gender = genders.get(0);
        System.out.println("Sending gender as = " + gender);
        Response response = given().
                queryParams("gender", gender).
                when().get().prettyPeek();

        response.then().
                assertThat().statusCode(200).and().
                contentType("application/json; charset=utf-8").
                and().body("gender", is(gender));
    }

    /**
     * 2 params test
     * 1.Create a request by providing query parameters: a valid region and genderNOTE:
     * Available region values are given in the documentation
     * 2.Verify status code 200, content type application/json;
     * charset=utf-8
     * 3.Verify that value of gender field is same from step 1
     * 4.Verify that value of region field is same from step 1
     */

    @DisplayName("2 params test")
    @Test
    public void twoParamsTest(){

        Response response = given().queryParam("region", "Belgium").
                queryParam("gender", "female").
                when().get().prettyPeek();

        response.then().
                assertThat().statusCode(200).and().
                contentType("application/json; charset=utf-8").
                and().body("gender", is("female")).
                body("region", is("Belgium"));
    }

    /**
     * Invalid gender test1.Create a request by providing query parameter: invalid gender
     * 2.Verify status code 400 and status line contains Bad Request
     * 3.Verify that value of error field is Invalid gender
     */
    @Test
    @DisplayName("Invalid gender test")
    public void invalidGender(){
        Response response = given().queryParam("gender", "dog").
                when().get().prettyPeek();

        response.then().
                assertThat().statusCode(400).
                and().statusLine(containsString("Bad request")).
                and().body("error", is("Invalid gender"));
    }

    /**
     * Invalid region test
     * 1.Create a request by providing query parameter: invalid region
     * 2.Verify status code 400 and status line contains Bad Request
     * 3.Verify that value of error field is Regionorlanguagenotfound
     */
    @Test
    @DisplayName("Invalid region name")
    public void invalidRegion(){
        Response response = given().
                queryParam("region", "Miami").
                get().prettyPeek();

        response.then().
                assertThat().statusCode(400).
                and().statusLine(containsString("Bad request")).
                and().body("error", is("Region or language not found"));
    }

    /**
     * Amount and regions test1.Create request by providing query parameters:
     * a valid region and amount(must be bigger than 1)
     * 2.Verify status code 200, content type application/json; charset=utf-8
     * 3.Verify that all objects have different name+surname combination
     */
  @Test
  @DisplayName("Amount and region test")
   public void amountAndRegion() {
      Response response = given().
              queryParam("region", "Germany").
              queryParam("amount", 40).
              when().
              get().prettyPeek();

      List<User> userList = response.jsonPath().getList("", User.class);
      System.out.println("userList :: " + userList);

      Set<String> fullNames = new HashSet<>();
      for (User user:userList) {
          String fullName = user.getName() + " " + user.getSurname();
                  fullNames.add(fullName);
      }

      //2nd way to collect all the first and last names in collection
      Set<String> fullNamesV2 = userList.stream().
              map(user -> user.getName()+ " " + user.getSurname()).collect(Collectors.toSet());

       response.then().assertThat().
               statusCode(200).and().
               header("Content-Type", "application/json; charset=utf-8").
               and().body("size()", is(fullNamesV2.size()));
   }

    /**
     * 3 params test
     * 1.Create a request by providing query parameters:
     * a valid region, gender and amount (must be biggerthan 1)
     * 2.Verify status code 200, content type application/json; charset=utf-8
     * 3.Verify that all objects the response have the same region and gender
     * passed in step 1
     */
    public String generaterandomGender (){
        Collections.shuffle(genders);
        return genders.get(0);
    }

    int randomAmount = new Random().nextInt(500)+1;//creates numbers form 0 to 499
    private final List<String> genders = Arrays.asList("male", "female");

    //getProperty("user.dir") will provide project path
    File namesJson = new File(System.getProperty("user.dir")+File.separator + "names.json");
   JsonPath jsonPath = new JsonPath(namesJson);
    List<String> regions = jsonPath.getList("region");

    public String generateRandomRegion(){
        Collections.shuffle(regions);
        return regions.get(0);
    }



    @Test
   @DisplayName("three parameters")
    public void threeParamsTest(){
       String randomGender = generaterandomGender();
       String randomRegion = generateRandomRegion();

        System.out.println("randomRegion = " + randomRegion);
        System.out.println("randomGender = " + randomGender);
        System.out.println("randomAmount = " + randomAmount);
      Response response = given().queryParam("region", randomRegion).
              queryParam("gender", randomGender).
              queryParam("amount", randomAmount).when().get().prettyPeek();

      response.then().
              assertThat().statusCode(200).and().
              contentType("application/json; charset=utf-8").and().
              body("size()", is(randomAmount)).
              body("gender", everyItem(is(randomGender))).
              body("region", everyItem(is(randomRegion)));
   }

    /**
     * Amount count test
     * 1.Create a request by providing query parameter: amount (must be bigger than 1)
     * 2.Verify status code 200, content type application/json; charset=utf-8
     * 3.Verify that number of objects returned in the response is same as
     * the amount passed in step 1
     */

   @Test
   @DisplayName("Amount count test")
   public void amountCount(){
       System.out.println("Random amount = " + randomAmount);
        Response response = given().
                queryParam("amount", randomAmount).
                when().
                get().prettyPeek();

        response.then().
                assertThat().
                statusCode(200).
                and().
                contentType("application/json; charset=utf-8").
                and().body("size()", is(randomAmount));
   }

}
