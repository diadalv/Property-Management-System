package io.github.diadalv.pms.guest;

public record Guest(String firstName, String lastName, String email, String phone) {

    public Guest {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}