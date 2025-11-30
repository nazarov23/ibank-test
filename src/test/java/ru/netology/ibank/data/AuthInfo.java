package ru.netology.ibank.data;

import lombok.Value;

@Value
public class AuthInfo {
    String login;
    String password;
}