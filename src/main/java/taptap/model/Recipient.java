package taptap.model;

public record Recipient(String firstName, String lastName, String email, String phone,
        String nickname, String wallet, double amount) {}