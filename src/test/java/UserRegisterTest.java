import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.UserClient;
import org.example.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class UserRegisterTest {
    User user;
    UserClient userClient;
    String accessToken;

    @Before
    public void setUp(){
        user = UserGenerate.getRandom();
        userClient = new UserClient();
    }

    @After
    public void tearDown(){
        ValidatableResponse responseDelete = userClient.deleteUser(accessToken);
        System.out.println(responseDelete.extract().path("message").toString());
    }

    @Test
    @DisplayName("User registers")
    @Description("Basic test for /api/auth/register")
    public void checkRegisterUserSuccess(){
        ValidatableResponse responseRegister = userClient.registerUser(user);
        System.out.println("Create user "+user.getEmail());
        int statusCode = responseRegister.extract().statusCode();

        Assert.assertEquals("Expected 200", SC_OK, statusCode);
        if (statusCode == SC_OK) {
            accessToken = responseRegister.extract().path("accessToken");
            HashMap <String, String> registerData = responseRegister.extract().path("user");

            Assert.assertTrue(responseRegister.extract().path("success"));
            Assert.assertNotNull(accessToken);
            Assert.assertEquals(user.getEmail(), registerData.get("email"));
            Assert.assertEquals(user.getName(), registerData.get("name"));
            Assert.assertNotNull(responseRegister.extract().path("refreshToken"));
        }
    }

    @Test
    @DisplayName("Duplicate user registers")
    @Description("Check error when we register user which already exists")
    public void checkRegisterDuplicateUser(){
        ValidatableResponse responseRegisterFirst = userClient.registerUser(user);
        accessToken = responseRegisterFirst.extract().path("accessToken");
        ValidatableResponse responseRegister = userClient.registerUser(user);
        System.out.println("Create user "+user.getEmail());
        int statusCode = responseRegister.extract().statusCode();

        Assert.assertEquals("Expected 403", SC_FORBIDDEN, statusCode);
        Assert.assertFalse(responseRegister.extract().path("success"));
        Assert.assertEquals("User already exists", responseRegister.extract().path("message"));
    }
}
