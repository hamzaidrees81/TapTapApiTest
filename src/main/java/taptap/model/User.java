package taptap.model;


public record User(UserCredentails userAuth, String firstName, String lastName, String email, String password) {};