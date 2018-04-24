package org.tensorflow.demo;


import java.io.Serializable;
/***********soufiane*****************/
public class Volontaire implements Serializable{
    public String id;
    public String email;
    public String email2;
    public String call;
    public String rec2;
    public String points;
    public Volontaire(String id, String email, String email2, String call, String rec2,String points)
    {
        this.id=id;
        this.email=email;
        this.email2=email2;
        this.call=call;
        this.rec2=rec2;
        this.points=points;

    }
    public Volontaire()
    {

    }
}
