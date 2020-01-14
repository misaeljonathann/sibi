package com.fasilkom.sibi.models;

public class PagerTutorialModel {
    String id;
    String desc;
    Integer img;
    public PagerTutorialModel(String id, String desc, Integer img){
        this.id = id;
        this.desc = desc;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }
}
