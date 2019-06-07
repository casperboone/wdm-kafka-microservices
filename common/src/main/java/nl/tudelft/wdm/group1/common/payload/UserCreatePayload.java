package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class UserCreatePayload extends RestPayload {
    private UUID id;
    private String firstName;
    private String lastName;
    private String street;
    private String zip;
    private String city;

    public UserCreatePayload() {

    }

    public UserCreatePayload(UUID id, String firstName, String lastName, String street, String zip, String city) {
        this.id = id;
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
}
