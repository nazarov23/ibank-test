package ru.netology.ibank;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.ibank.data.AuthInfo;
import ru.netology.ibank.data.DataGenerator;
import ru.netology.ibank.data.RegistrationDto;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AuthTest {

    @BeforeEach
    void setup() {
        // Настройка Selenide
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = true;
        Configuration.timeout = 15000;

        // Открываем страницу интернет-банка
        open("http://localhost:9999");
    }

    // ТЕСТ 1: Успешная авторизация активного пользователя
    @Test
    void shouldLoginWithActiveUser() {
        // Регистрируем активного пользователя через API
        AuthInfo authInfo = DataGenerator.getRegisteredActiveUser();

        // Заполняем форму авторизации
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        // Проверяем успешный вход
        $("h2").shouldHave(exactText("Личный кабинет"))
                .shouldBe(visible);
    }

    // ТЕСТ 2: Неуспешная авторизация заблокированного пользователя
    @Test
    void shouldNotLoginWithBlockedUser() {
        // Регистрируем заблокированного пользователя через API
        AuthInfo authInfo = DataGenerator.getRegisteredBlockedUser();

        // Заполняем форму авторизации
        $("[data-test-id=login] input").setValue(authInfo.getLogin());
        $("[data-test-id=password] input").setValue(authInfo.getPassword());
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение об ошибке
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Пользователь заблокирован"))
                .shouldBe(visible);
    }

    // ТЕСТ 3: Неуспешная авторизация с неверным логином
    @Test
    void shouldShowErrorWithInvalidLogin() {
        // Регистрируем активного пользователя
        AuthInfo registeredUser = DataGenerator.getRegisteredActiveUser();

        // Используем неверный логин
        $("[data-test-id=login] input").setValue(DataGenerator.generateInvalidLogin());
        $("[data-test-id=password] input").setValue(registeredUser.getPassword());
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение об ошибке
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Неверно указан логин или пароль"))
                .shouldBe(visible);
    }

    // ТЕСТ 4: Неуспешная авторизация с неверным паролем
    @Test
    void shouldShowErrorWithInvalidPassword() {
        // Регистрируем активного пользователя
        AuthInfo registeredUser = DataGenerator.getRegisteredActiveUser();

        // Используем неверный пароль
        $("[data-test-id=login] input").setValue(registeredUser.getLogin());
        $("[data-test-id=password] input").setValue(DataGenerator.generateInvalidPassword());
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение об ошибке
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Неверно указан логин или пароль"))
                .shouldBe(visible);
    }

    // ТЕСТ 5: Неуспешная авторизация с пустым логином
    @Test
    void shouldShowErrorWithEmptyLogin() {
        // Оставляем логин пустым
        $("[data-test-id=password] input").setValue("anypassword");
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение об ошибке валидации
        $("[data-test-id=login].input_invalid .input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    // ТЕСТ 6: Неуспешная авторизация с пустым паролем
    @Test
    void shouldShowErrorWithEmptyPassword() {
        // Регистрируем активного пользователя
        AuthInfo registeredUser = DataGenerator.getRegisteredActiveUser();

        // Оставляем пароль пустым
        $("[data-test-id=login] input").setValue(registeredUser.getLogin());
        $("[data-test-id=action-login]").click();

        // Проверяем сообщение об ошибке валидации
        $("[data-test-id=password].input_invalid .input__sub")
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    // ТЕСТ 7: Перезапись данных пользователя (дополнительный тест)
    @Test
    void shouldOverwriteUserData() {
        // Создаем первого пользователя
        RegistrationDto firstUser = DataGenerator.generateUser("active");
        DataGenerator.setUpUser(firstUser);

        // Создаем второго пользователя с тем же логином, но другим паролем и статусом
        RegistrationDto secondUser = new RegistrationDto(
                firstUser.getLogin(),
                "newpassword",
                "blocked"
        );
        DataGenerator.setUpUser(secondUser); // Должен перезаписать первого пользователя

        // Пытаемся войти с новыми данными
        $("[data-test-id=login] input").setValue(secondUser.getLogin());
        $("[data-test-id=password] input").setValue(secondUser.getPassword());
        $("[data-test-id=action-login]").click();

        // Должны получить ошибку, так как пользователь теперь заблокирован
        $("[data-test-id=error-notification] .notification__content")
                .shouldHave(text("Пользователь заблокирован"))
                .shouldBe(visible);
    }
}