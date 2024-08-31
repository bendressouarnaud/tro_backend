package com.ankk.tro.testrestemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GtrApiSendRequest {
    private String sourceSoftware;
    private String eventDatetime;
    private String generatedDatetime;
    private String eventCode;
    private String orderNumber;
    private int tmsStopOrder;
    private int eventType;
    private int status;
    private int severity;
    private double latitude;
    private double longitude;
    private String description;
    private boolean isAlert;
    private boolean enable;

    public void setSourceSoftware(String sourceSoftware) {
        this.sourceSoftware = sourceSoftware;
    }

    public void setEventDatetime(String eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setTmsStopOrder(int tmsStopOrder) {
        this.tmsStopOrder = tmsStopOrder;
    }

    public String getSourceSoftware() {
        return sourceSoftware;
    }

    public String getEventDatetime() {
        return eventDatetime;
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getTmsStopOrder() {
        return tmsStopOrder;
    }

    public String getGeneratedDatetime() {
        return generatedDatetime;
    }

    public void setGeneratedDatetime(String generatedDatetime) {
        this.generatedDatetime = generatedDatetime;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("isAlert")
    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
