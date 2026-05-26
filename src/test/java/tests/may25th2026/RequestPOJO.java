package tests.may25th2026;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

//POJO --> Plain Old Java Object
//That particular class will consist of setter and getter methods only. No business logic at all

//@Getter and @Setter Annotations from lombok are used to generate the getter and setter methods for each and every variable that we have automatically
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE) //Ensures that all the variables are having private access modifier by default
@Accessors(chain = true) //Ensures the concept of method chaining across all the setter methods
public class RequestPOJO implements RequestData {

    //Here we are maintaining the variable name, same as the JSON keys so that during the time of serialization, proper mapping will be done
    @JsonProperty("account_holder_name") //Here we are passing the actual JSON key
    String accountHolderName; //Here we can maintain the variable name of your choice

    @JsonProperty("initial_balance")
    int initialBalance;

    @JsonProperty("account_type")
    String accountType;
    String currency;

    //@JsonIgnore
    //String email; //Ensures that this particular will not be part of the JSON Request
    String email;
    String phone;

    @JsonProperty("address_line1")
    String addressLine1;

    @JsonProperty("address_line2")
    String addressLine2;
    String city;
    String state;

    @JsonProperty("zip_code")
    String zipCode;
    String country;

//    public String getAccountHolderName() {
//        return accountHolderName;
//    }
//
//    public void setAccountHolderName(String accountHolderName) {
//        this.accountHolderName = accountHolderName;
//    }
//
//    public int getInitialBalance() {
//        return initialBalance;
//    }
//
//    public void setInitialBalance(int initialBalance) {
//        this.initialBalance = initialBalance;
//    }
//
//    public String getAccountType() {
//        return accountType;
//    }
//
//    public void setAccountType(String accountType) {
//        this.accountType = accountType;
//    }
//
//    public String getAddressLine1() {
//        return addressLine1;
//    }
//
//    public void setAddressLine1(String addressLine1) {
//        this.addressLine1 = addressLine1;
//    }
//
//    public String getAddressLine2() {
//        return addressLine2;
//    }
//
//    public void setAddressLine2(String addressLine2) {
//        this.addressLine2 = addressLine2;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getZipCode() {
//        return zipCode;
//    }
//
//    public void setZipCode(String zip_code) {
//        this.zipCode = zip_code;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }

}
