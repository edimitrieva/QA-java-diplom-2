package org.example.methods;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.Client;
import org.example.model.IngredientsList;
import org.example.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    private static final String PATH_ORDER = "/api/orders";

    @Step("Create order")
    public ValidatableResponse createOrder(IngredientsList ingredients, String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .and()
                .body(ingredients)
                .when()
                .post(PATH_ORDER)
                .then();
    }

    @Step("Get user's order")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .when()
                .get(PATH_ORDER)
                .then();
    }
}
