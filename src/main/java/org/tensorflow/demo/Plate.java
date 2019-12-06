package org.tensorflow.demo;

import androidx.annotation.Nullable;

public class Plate {
    private String type = "Car";
    private String validity = "28.12.2019";
    private String owner = "John Doe";
    private String location;
    private String date;
    private String text;
    private String imagePath;
    private String _ID;

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

    public String getValidity() {
        return validity;
    }

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


}

