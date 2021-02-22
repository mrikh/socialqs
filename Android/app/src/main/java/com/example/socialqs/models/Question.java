package com.example.socialqs.models;

public class Question {

    private String qId;
    private String qTitle;
    private String qCategory;

    public Question() {
        qId = "";
        qTitle = "";
        qCategory = "";
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getqTitle() {
        return qTitle;
    }

    public void setqTitle(String qTitle) {
        this.qTitle = qTitle;
    }

    public String getqCategory() {
        return qCategory;
    }

    public void setqCategory(String qCategory) {
        this.qCategory = qCategory;
    }
}