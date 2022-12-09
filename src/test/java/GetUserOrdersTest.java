import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.OrderClient;
import org.example.methods.UserClient;
import org.example.model.IngredientsList;
import org.example.model.OrderSuccess;
import org.example.model.User;
import org.example.model.UserOrders;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@DisplayName("Get user's orders")
public class GetUserOrdersTest {
    User user;
    UserClient userClient;
    String accessToken;
    OrderClient orderClient;
    int number;

    @Before
    public void setUp() {
        user = UserGenerate.getRandom();
        userClient = new UserClient();
        ValidatableResponse response = userClient.registerUser(user);
        accessToken = response.extract().path("accessToken");
        System.out.println("Login user " + user.getEmail());

        orderClient = new OrderClient();
        List<String> ingredientsList = List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6d");
        IngredientsList ingredients = new IngredientsList(ingredientsList);
        ValidatableResponse responseOrder = orderClient.createOrder(ingredients, accessToken);
        OrderSuccess orderSuccess = responseOrder.extract().as(OrderSuccess.class);
        number = orderSuccess.getOrder().getNumber();
    }

    @After
    public void tearDown() {
        ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
        System.out.println(responseDelete.extract().path("message").toString());
    }

    @Test
    @DisplayName("Get user's orders")
    @Description("Basic tests for GET /api/orders")
    public void getUserOrdersTestSuccess() {
        ValidatableResponse responseGetOrders = orderClient.getUserOrders(accessToken);
        int statusCode = responseGetOrders.extract().statusCode();

        Assert.assertEquals(SC_OK, statusCode);
        if (statusCode == SC_OK) {
            UserOrders userOrders = responseGetOrders.extract().as(UserOrders.class);
            Assert.assertTrue(userOrders.isSuccess());
            Assert.assertEquals(number, userOrders.getOrders().get(0).getNumber());
            System.out.println("Номер заказа " + number);
        }
    }

    @Test
    @DisplayName("Get user's orders without auth")
    @Description("Check error when we get user's orders without token")
    public void getUserOrdersTestWithoutAuth() {
        ValidatableResponse responseGetOrders = orderClient.getUserOrders("");

        Assert.assertEquals(SC_UNAUTHORIZED, responseGetOrders.extract().statusCode());
        Assert.assertFalse(responseGetOrders.extract().path("success"));
        Assert.assertEquals("You should be authorised", responseGetOrders.extract().path("message"));
    }
}
