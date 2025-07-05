
package com.example.project.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String categoryName;

    public Category() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
