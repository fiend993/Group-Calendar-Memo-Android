package com.coms5540.calendarmemo.Utilities;

import java.io.Serializable;

//An Event entity, set of attribute and get and set method
public class Event implements Serializable {
    private String title;
    private String description;
    private String date;
    private String createdBy;
    private String group;
    private String createdAt;
    private String updateAt;

    private String id;

    public Event(String title, String description, String date, String createdBy, String group, String createdAt, String updateAt, String id){
        this.title = title;
        this.description = description;
        this.date = date;
        this.createdBy = createdBy;
        this.group = group;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getGroup() {
        return group;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }
}
