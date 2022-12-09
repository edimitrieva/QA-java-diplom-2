import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.UserClient;
import org.example.model.Credential;
import org.example.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@RunWith(Parameterized.class)
@DisplayName("Error's login")
public class UserLoginErrorTest {
    User user;
    UserClient userClient;
    int expectedCode;
    String expectedErrorMessage;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    public UserLoginErrorTest(User user) {
        this.user = user;
        this.expectedCode = SC_UNAUTHORIZED;
        this.expectedErrorMessage = "email or password are incorrect";
    }

    @Parameterized.Parameters(name = "Тестовые данные: User = {0}")
    public static Object[][] getTestData() {
        return new Object[][]{
                {UserGenerate.getWithoutEmail()},
                {UserGenerate.getWithoutPassword()},
                {UserGenerate.getRandom()}
        };
    }

    @Test
    @DisplayName("User login with wrong user's data")
    @Description("Check error when we logs in with wrong user's data")
    public void checkRegisterUserError() {
        ValidatableResponse responseLogin = userClient.loginUser(Credential.from(user));

        Assert.assertEquals(expectedCode, responseLogin.extract().statusCode());
        Assert.assertFalse(responseLogin.extract().path("success"));
        Assert.assertEquals(expectedErrorMessage, responseLogin.extract().path("message"));
    }
}
