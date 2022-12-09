import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.OrderClient;
import org.example.methods.UserClient;
import org.example.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;

@DisplayName("Create order")
public class OrderCreateTest {
    User user;
    UserClient userClient;
    String accessToken;
    OrderClient orderClient;

    @Before
    public void setUp() {
        user = UserGenerate.getRandom();
        userClient = new UserClient();
        ValidatableResponse response = userClient.registerUser(user);
        accessToken = response.extract().path("accessToken");
        System.out.println("Login user " + user.getEmail());

        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
        System.out.println(responseDelete.extract().path("message").toString());
    }

    @Test
    @DisplayName("Create order with auth")
    @Description("Basic tests for POST /api/orders with auth")
    public void checkCreateOrderWithAuthSuccess() {
        List<String> ingredientsList = List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6d");
        IngredientsList ingredients = new IngredientsList(ingredientsList);
        ValidatableResponse responseOrder = orderClient.createOrder(ingredients, accessToken);
        int statusCode = responseOrder.extract().statusCode();

        Assert.assertEquals(SC_OK, statusCode);
        if (statusCode == SC_OK) {
            OrderSuccess orderSuccess = responseOrder.extract().as(OrderSuccess.class);
            Assert.assertTrue(orderSuccess.isSuccess());
            Assert.assertEquals("Метеоритный флюоресцентный spicy бургер", orderSuccess.getName());
            System.out.println(orderSuccess.getName());

            Order order = orderSuccess.getOrder();
            Assert.assertNotNull(order.getNumber());
            System.out.println("Номер заказа " + order.getNumber() + " по цене " + order.getPrice());
        }
    }

    @Test
    @DisplayName("Create order without auth")
    @Description("Basic tests for POST /api/orders without auth")
    public void checkCreateOrderWithoutAuthSuccess() {
        List<String> ingredientsList = List.of("61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa6d");
        IngredientsList ingredients = new IngredientsList(ingredientsList);
        ValidatableResponse responseOrder = orderClient.createOrder(ingredients, "");
        int statusCode = responseOrder.extract().statusCode();

        Assert.assertEquals(SC_OK, statusCode);
        if (statusCode == SC_OK) {
            OrderSuccess orderSuccess = responseOrder.extract().as(OrderSuccess.class);
            Assert.assertTrue(orderSuccess.isSuccess());
            Assert.assertEquals("Метеоритный флюоресцентный spicy бургер", orderSuccess.getName());
            System.out.println(orderSuccess.getName());
            Assert.assertNotNull(orderSuccess.getOrder().getNumber());
            System.out.println("Номер заказа " + orderSuccess.getOrder().getNumber());
            Assert.assertNull(orderSuccess.getOrder().get_id());
        }
    }

    @Test
    @DisplayName("Create order without ingredients")
    @Description("Check error when we create an order without ingredients")
    public void checkCreateOrderWithoutIngredients() {
        List<String> ingredientsList = List.of();
        IngredientsList ingredients = new IngredientsList(ingredientsList);
        ValidatableResponse responseOrder = orderClient.createOrder(ingredients, accessToken);
        int statusCode = responseOrder.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
        Assert.assertFalse(responseOrder.extract().path("success"));
        Assert.assertEquals("Ingredient ids must be provided", responseOrder.extract().path("message"));
    }

    @Test
    @DisplayName("Create order with wrong ingredients")
    @Description("Check error when we create an order with wrong ingredient's hash")
    public void checkCreateOrderWithWrongIngredients() {
        List<String> ingredientsList = List.of("123456");
        IngredientsList ingredients = new IngredientsList(ingredientsList);
        ValidatableResponse responseOrder = orderClient.createOrder(ingredients, accessToken);
        int statusCode = responseOrder.extract().statusCode();

        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}
