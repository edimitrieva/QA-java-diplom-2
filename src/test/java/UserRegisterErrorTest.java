import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.generator.UserGenerate;
import org.example.methods.UserClient;
import org.example.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;

@RunWith(Parameterized.class)
public class UserRegisterErrorTest {
    User user;
    UserClient userClient;
    int expectedCode;
    String expectedErrorMessage;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    public UserRegisterErrorTest(User user) {
        this.user = user;
        this.expectedCode = SC_FORBIDDEN;
        this.expectedErrorMessage = "Email, password and name are required fields";
    }

    @Parameterized.Parameters(name = "Тестовые данные: User = {0}")
    public static Object[][] getTestData() {
        return new Object[][]{
                {UserGenerate.getWithoutEmail()},
                {UserGenerate.getWithoutPassword()},
                {UserGenerate.getWithoutName()}
        };
    }

    @Test
    @DisplayName("User registers with wrong data")
    @Description("Check error when we register with wrong data")
    public void checkRegisterUserError() {
        ValidatableResponse responseRegister = userClient.registerUser(user);

        Assert.assertEquals(expectedCode, responseRegister.extract().statusCode());
        Assert.assertFalse(responseRegister.extract().path("success"));
        Assert.assertEquals(expectedErrorMessage, responseRegister.extract().path("message"));
    }
}
