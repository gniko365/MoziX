package com.mycompany.mozixx.utils;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {

    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        // Legalább 8 karakter
        if (password.length() < 8) {
            errors.add("A jelszónak legalább 8 karakter hosszúnak kell lennie.");
        }

        // Tartalmaz nagybetűt
        if (!password.matches(".*[A-Z].*")) {
            errors.add("A jelszónak tartalmaznia kell legalább egy nagybetűt.");
        }

        // Tartalmaz kisbetűt
        if (!password.matches(".*[a-z].*")) {
            errors.add("A jelszónak tartalmaznia kell legalább egy kisbetűt.");
        }

        // Tartalmaz számot
        if (!password.matches(".*\\d.*")) {
            errors.add("A jelszónak tartalmaznia kell legalább egy számot.");
        }

        // Tartalmaz speciális karaktert
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.add("A jelszónak tartalmaznia kell legalább egy speciális karaktert.");
        }

        return errors;
    }
}