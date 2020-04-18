package org.tensorflow.demo;



public class Shop {
    private String _ID;
    private String imagePath;
    private String name;
    private String manager;
    private String phone;
    private String location;
    private String type;
    private String license;


    public Shop(){}
    public Shop(String _ID){
        this._ID = _ID;
    }

    public Shop(String name,String imagePath,String manager,String phone,String location, String type,String license){
        this.imagePath = imagePath;
        this.name = name;
        this.manager = manager;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.license = license;
    }

    public Shop(String _ID,String name,String imagePath,String manager,String phone,String location, String type,String license){
        this.imagePath = imagePath;
        this.name = name;
        this.manager = manager;
        this.phone = phone;
        this.location = location;
        this.type = type;
        this.license = license;
        this._ID = _ID;
    }


    public String get_ID() {
        return _ID;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLicense() {
        return license;
    }

    public String getManager() {
        return manager;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public void setLicense(String license) {
        this.license = license;
    }


    public void setManager(String manager) {
        this.manager = manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

