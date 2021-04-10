package com.example.socialqs.models;

/**
 *  Information for each question item
 */
public class QuestionModel {

    private String qId;
    private String qTitle;
    private CategoryModel qCategory;

    public QuestionModel() {
        qId = "";
        qTitle = "";
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

    public CategoryModel getqCategory() {
        return qCategory;
    }

    public void setqCategory(CategoryModel qCategory) {
        this.qCategory = qCategory;
    }
}