import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.UserClient;
import org.example.model.User;
import org.example.model.UserProfile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class UserProfileTest {
    User user;
    UserClient userClient;
    String accessToken;

    @Before
    public void setUp() {
        user = UserGenerate.getRandom();
        userClient = new UserClient();
        ValidatableResponse response = userClient.registerUser(user);
        accessToken = response.extract().path("accessToken");
        System.out.println("Login user " + user.getEmail());
    }

    @After
    public void tearDown() {
        ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
        System.out.println(responseDelete.extract().path("message").toString());
    }

    @Test
    @DisplayName("Get user's profile")
    @Description("Basic test for GET /api/auth/user")
    public void getUserProfileWithAuth() {
        ValidatableResponse responseProfile = userClient.userProfile(accessToken);
        int statusCode = responseProfile.extract().statusCode();

        Assert.assertEquals("Expected 200", SC_OK, statusCode);
        if (statusCode == SC_OK) {
            HashMap<String, String> registerData = responseProfile.extract().path("user");

            Assert.assertTrue(responseProfile.extract().path("success"));
            Assert.assertEquals(user.getEmail(), registerData.get("email"));
            Assert.assertEquals(user.getName(), registerData.get("name"));
        }
    }

    @Test
    @DisplayName("Get user's profile without auth")
    @Description("Check error when we get user's profile without auth")
    public void getUserProfileWithoutAuth() {
        ValidatableResponse responseProfile = userClient.userProfile(" ");

        Assert.assertEquals(SC_UNAUTHORIZED, responseProfile.extract().statusCode());
        Assert.assertFalse(responseProfile.extract().path("success"));
        Assert.assertEquals("You should be authorised", responseProfile.extract().path("message"));
    }

    @Test
    @DisplayName("Change user's data")
    @Description("Basic test for PATCH /api/auth/user")
    public void changeUserDataSuccess() {
        String email = "new_" + System.currentTimeMillis() + "_apitest@test.ru";
        String name = "Anna";
        ValidatableResponse responseProfile = userClient.changeUserProfile(accessToken, new UserProfile(email, name));
        int statusCode = responseProfile.extract().statusCode();

        Assert.assertEquals("Expected 200", SC_OK, statusCode);
        if (statusCode == SC_OK) {
            HashMap<String, String> registerData = responseProfile.extract().path("user");

            Assert.assertTrue(responseProfile.extract().path("success"));
            Assert.assertEquals(email, registerData.get("email"));
            Assert.assertEquals(name, registerData.get("name"));
        }
    }

    @Test
    @DisplayName("Change user's data without auth")
    @Description("Check error when we change user's profile without auth")
    public void changeUserDataWithoutAuthFail() {
        String email = "new_" + System.currentTimeMillis() + "_apitest@test.ru";
        String name = "Anna";
        ValidatableResponse responseProfile = userClient.changeUserProfile(" ", new UserProfile(email, name));
        int statusCode = responseProfile.extract().statusCode();

        Assert.assertEquals("Expected 401", SC_UNAUTHORIZED, statusCode);
        Assert.assertFalse(responseProfile.extract().path("success"));
        Assert.assertEquals("You should be authorised", responseProfile.extract().path("message"));
    }
}
