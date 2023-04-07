package com.example.collegehelper.Model;

public class ItemModel {
    private String id,url,name,date,publisher,tags;
    private Boolean ispdf;
    ItemModel(){

    }

    public ItemModel(String url, String name, String date, String publisher, Boolean ispdf, Boolean isimage) {
        this.url = url;
        this.name = name;
        this.date = date;
        this.publisher = publisher;
        this.ispdf = ispdf;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Boolean getIspdf() {
        return ispdf;
    }

    public void setIspdf(Boolean ispdf) {
        this.ispdf = ispdf;
    }

}
