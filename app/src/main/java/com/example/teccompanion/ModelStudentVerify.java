package com.example.teccompanion;

public class ModelStudentVerify
{
    String FullName, ID, ImageUrl, Session;

    ModelStudentVerify()
    {

    }


    public ModelStudentVerify(String fullName, String ID, String imageUrl, String session) {
        FullName = fullName;
        this.ID = ID;
        ImageUrl = imageUrl;
        Session = session;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getSession() {
        return Session;
    }

    public void setSession(String session) {
        Session = session;
    }
}
