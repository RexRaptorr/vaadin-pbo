package com.example.application.data.entity;

import javax.persistence.Entity;

@Entity
public class Categories extends AbstractEntity {

    private Integer idCategories;
    private String namaCategories;

    public Integer getIdCategories() {
        return idCategories;
    }
    public void setIdCategories(Integer idCategories) {
        this.idCategories = idCategories;
    }
    public String getNamaCategories() {
        return namaCategories;
    }
    public void setNamaCategories(String namaCategories) {
        this.namaCategories = namaCategories;
    }

}
