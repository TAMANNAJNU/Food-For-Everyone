package com.example.foodforeveryone.model;

public class DonationDataModel {
    private String donationName;
    private String donationMobileNum;
    private String donationAddress;
    private String donationFoodDescription;
    private String pushKey;
    private String userID;
    private String userName;
    private String userMobileNo;
    private String userEmail;

    public DonationDataModel() {
    }

    public DonationDataModel(String donationName, String donationMobileNum, String donationAddress, String donationFoodDescription, String pushKey, String userID, String userName, String userMobileNo, String userEmail) {
        this.donationName = donationName;
        this.donationMobileNum = donationMobileNum;
        this.donationAddress = donationAddress;
        this.donationFoodDescription = donationFoodDescription;
        this.pushKey = pushKey;
        this.userID = userID;
        this.userName = userName;
        this.userMobileNo = userMobileNo;
        this.userEmail = userEmail;
    }

    public String getDonationName() {
        return donationName;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public String getDonationMobileNum() {
        return donationMobileNum;
    }

    public void setDonationMobileNum(String donationMobileNum) {
        this.donationMobileNum = donationMobileNum;
    }

    public String getDonationAddress() {
        return donationAddress;
    }

    public void setDonationAddress(String donationAddress) {
        this.donationAddress = donationAddress;
    }

    public String getDonationFoodDescription() {
        return donationFoodDescription;
    }

    public void setDonationFoodDescription(String donationFoodDescription) {
        this.donationFoodDescription = donationFoodDescription;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobileNo() {
        return userMobileNo;
    }

    public void setUserMobileNo(String userMobileNo) {
        this.userMobileNo = userMobileNo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
