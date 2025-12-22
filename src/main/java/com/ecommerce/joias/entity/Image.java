package com.ecommerce.joias.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Column(name = "parent_type", nullable = false)
    private String parentType;

    @Column(name = "is_main")
    private Boolean isMain = false;

    public Image(){

    }

    public Image(String url, String publicId, Integer parentId, String parentType, boolean isMain) {
        this.url = url;
        this.publicId = publicId;
        this.parentId = parentId;
        this.parentType = parentType;
        this.isMain = isMain;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}
