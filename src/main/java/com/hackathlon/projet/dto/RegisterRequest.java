package com.hackathlon.projet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 20, message = "Le nom d'utilisateur doit contenir entre 3 et 20 caractères")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores")
        private String username;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#]).{12,}$", message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&_-#)")
        private String password;
}
