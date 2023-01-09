package com.example.application.data.entity;

import javax.persistence.Entity;

@Entity
public class Subacetgory extends AbstractEntity {

    private Integer idSubcategories;
    private String namaSubcategories;

    public Integer getIdSubcategories() {
        return idSubcategories;
    }
    public void setIdSubcategories(Integer idSubcategories) {
        this.idSubcategories = idSubcategories;
    }
    public String getNamaSubcategories() {
        return namaSubcategories;
    }
    public void setNamaSubcategories(String namaSubcategories) {
        this.namaSubcategories = namaSubcategories;
    }

}
