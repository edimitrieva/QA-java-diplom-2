package org.example.methods;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.Client;
import org.example.model.Credential;
import org.example.model.User;
import org.example.model.UserProfile;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    private static final String PATH_REGISTER = "/api/auth/register";
    private static final String PATH_DELETE = "/api/auth/user";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_PROFILE = "/api/auth/user";

    @Step("Register user")
    public ValidatableResponse registerUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH_REGISTER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .when()
                .delete(PATH_DELETE)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUser(Credential user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH_LOGIN)
                .then();
    }

    @Step("Get user's profile")
    public ValidatableResponse userProfile(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .when()
                .get(PATH_PROFILE)
                .then();
    }

    @Step("Change user's profile")
    public ValidatableResponse changeUserProfile(String token, UserProfile userProfile) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .and()
                .body(userProfile)
                .when()
                .patch(PATH_PROFILE)
                .then();
    }
}
