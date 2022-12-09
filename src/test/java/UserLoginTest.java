import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.UserClient;
import org.example.model.Credential;
import org.example.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@DisplayName("Login")
public class UserLoginTest {
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
    @DisplayName("User login")
    @Description("Basic test for /api/auth/login")
    public void checkLoginUserSuccess() {
        ValidatableResponse responseLogin = userClient.loginUser(Credential.from(user));
        int statusCode = responseLogin.extract().statusCode();

        Assert.assertEquals("Expected 200", SC_OK, statusCode);
        if (statusCode == SC_OK) {
            accessToken = responseLogin.extract().path("accessToken");
            HashMap<String, String> registerData = responseLogin.extract().path("user");

            Assert.assertTrue(responseLogin.extract().path("success"));
            Assert.assertNotNull(accessToken);
            Assert.assertEquals(user.getEmail(), registerData.get("email"));
            Assert.assertEquals(user.getName(), registerData.get("name"));
            Assert.assertNotNull(responseLogin.extract().path("refreshToken"));
        }
    }

    @Test
    @DisplayName("User login with wrong password")
    @Description("Check error when we logs in with wrong password")
    public void checkLoginWrongPassword() {
        ValidatableResponse responseLogin = userClient.loginUser(new Credential(user.getEmail(), "12345"));

        Assert.assertEquals(SC_UNAUTHORIZED, responseLogin.extract().statusCode());
        Assert.assertFalse(responseLogin.extract().path("success"));
        Assert.assertEquals("email or password are incorrect", responseLogin.extract().path("message"));
    }
}
