package nl.tudelft.wdm.group1.common.payload;

public class UserCreatePayload extends RestPayload {
    private String firstName;
    private String lastName;
    private String street;
    private String zip;
    private String city;

    public UserCreatePayload() {

    }

    public UserCreatePayload(String firstName, String lastName, String street, String zip, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.zip = zip;
        this.city = city;
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
