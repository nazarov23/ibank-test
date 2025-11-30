package ru.netology.ibank.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

import java.util.Locale;

import static io.restassured.RestAssured.given;

@UtilityClass
public class DataGenerator {
    // Спецификация для REST-запросов
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private static Faker faker = new Faker(new Locale("en"));

    /**
     * Генерирует случайного пользователя с указанным статусом
     */
    public static RegistrationDto generateUser(String status) {
        return new RegistrationDto(
                faker.name().username().toLowerCase(),
                faker.internet().password(),
                status
        );
    }

    /**
     * Регистрирует пользователя через API
     */
    public static void setUpUser(RegistrationDto user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    /**
     * Создает и регистрирует активного пользователя
     */
    public static AuthInfo getRegisteredActiveUser() {
        RegistrationDto activeUser = generateUser("active");
        setUpUser(activeUser);
        return new AuthInfo(activeUser.getLogin(), activeUser.getPassword());
    }

    /**
     * Создает и регистрирует заблокированного пользователя
     */
    public static AuthInfo getRegisteredBlockedUser() {
        RegistrationDto blockedUser = generateUser("blocked");
        setUpUser(blockedUser);
        return new AuthInfo(blockedUser.getLogin(), blockedUser.getPassword());
    }

    /**
     * Генерирует неверный логин (незарегистрированный пользователь)
     */
    public static String generateInvalidLogin() {
        return faker.name().username().toLowerCase();
    }

    /**
     * Генерирует неверный пароль
     */
    public static String generateInvalidPassword() {
        return faker.internet().password();
    }

    /**
     * Получает AuthInfo из RegistrationDto
     */
    public static AuthInfo getAuthInfo(RegistrationDto user) {
        return new AuthInfo(user.getLogin(), user.getPassword());
    }
}