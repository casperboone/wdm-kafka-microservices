package nl.tudelft.wdm.group1.common;

import java.util.UUID;

public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String street;
    private String zip;
    private String city;
    private int credit;

    public User() {
    }

    public User(String firstName, String lastName, String street, String zip, String city) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.zip = zip;
        this.city = city;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public int getCredit() {
        return credit;
    }

    public void addCredit(int amount) throws CreditChangeInvalidException {
        if (amount < 0) {
            throw new CreditChangeInvalidException("A negative credit amount cannot be added.");
        }
        credit += amount;
    }

    public void subtractCredit(int amount) throws InsufficientCreditException, CreditChangeInvalidException {
        if (amount < 0) {
            throw new CreditChangeInvalidException("A negative credit amount cannot be subtracted.");
        }
        if (amount > credit) {
            throw new InsufficientCreditException("Insufficient balance.");
        }
        credit -= amount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", credit=" + credit +
                '}';
    }
}
