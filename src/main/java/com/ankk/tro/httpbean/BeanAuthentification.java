package com.ankk.tro.httpbean;

public class BeanAuthentification {

    String mail, pwd, fcmtoken;
    int smartphonetype;

    public BeanAuthentification() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public int getSmartphonetype() {
        return smartphonetype;
    }

    public void setSmartphonetype(int smartphonetype) {
        this.smartphonetype = smartphonetype;
    }
}
