package ru.netology.ibank.data;

import lombok.Value;

@Value
public class RegistrationDto {
    String login;
    String password;
    String status;
}