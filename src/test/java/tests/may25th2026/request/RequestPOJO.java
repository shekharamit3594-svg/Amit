package tests.may25th2026.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import tests.may25th2026.RequestData;

//POJO --> Plain Old Java Object
//That particular class will consist of setter and getter methods only. No business logic at all

//@Getter and @Setter Annotations from lombok are used to generate the getter and setter methods for each and every variable that we have automatically
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE) //Ensures that all the variables are having private access modifier by default
@Accessors(chain = true) //Ensures the concept of method chaining across all the setter methods
public class RequestPOJO implements RequestData {

    @JsonProperty("account_holder_name") //Here we are passing the actual JSON key
    @NonNull
    String accountHolderName; //Here we can maintain the variable name of your choice

    @JsonProperty("initial_balance")
    @NonNull
    int initialBalance;

    @JsonProperty("account_type")
    String accountType;
    String currency;

    //@JsonIgnore // Uncomment to exclude email from the JSON request
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
