package org.tensorflow.demo;

import androidx.annotation.Nullable;

public class Plate {
    private String type = "Car vehicule";
    private String validity = "28-11-2019";
    private String owner ;
    private String location;
    private String date;
    private String text;
    private String imagePath;
    private String _ID;
    private int existence = 2;
    private String mongoid = "_";


    public Plate(){}

    public Plate(String _ID){
        this._ID = _ID;
    }
    public Plate(String location, String date, String text, String imagePath) {
        this.location = location;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;

    }
    public Plate(String location, String date, String text, String imagePath,int existence) {
        this.location = location;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
        this.existence=existence;
    }

    public Plate(String _ID,String location, String date, String text, String imagePath) {
        this.location = location;
        this._ID = _ID;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
    }
    public Plate(String _ID,String location, String date, String text, String imagePath,String owner,String validity,String type) {
        this.location = location;
        this._ID = _ID;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
        this.owner = owner;
        this.validity = validity;
        this.type = type;
    }

    public Plate(String _ID,String location, String date, String text, String imagePath,String owner,String validity,String type,int existence) {
        this.location = location;
        this._ID = _ID;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
        this.owner = owner;
        this.validity = validity;
        this.type = type;
        this.existence = existence;
    }

    public Plate(String _ID,String location, String date, String text, String imagePath,String owner,String validity,String type,String mongoid,int existence) {
        this.location = location;
        this._ID = _ID;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
        this.owner = owner;
        this.validity = validity;
        this.type = type;
        this.mongoid = mongoid;
        this.existence = existence;
    }

    public Plate(int existence,String location, String date, String text, String imagePath,String owner, String validity, String type, String mongoid){
        this.location = location;
        this.date = date;
        this.text = text;
        this.imagePath = imagePath;
        this.owner = owner;
        this.validity = validity;
        this.type = type;
        this.mongoid = mongoid;
        this.existence = existence;
    }



    public String get_ID() {
        return _ID;
    }

    public String getDate() {
        return date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLocation() {
        return location;
    }

    public String getOwner() {
        return owner;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public String getMongoid() {
        return this.mongoid;
    }

    public String getValidity() {
        return validity;
    }

    public Integer getExistence(){return existence;}

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public void setMongoid(String mongoid) {
        this.mongoid = mongoid;
    }

    public void setExistence(int existence) {
        this.existence = existence;
    }


}

