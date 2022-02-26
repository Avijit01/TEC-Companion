package com.example.teccompanion;

public class ModelTeacherVerify
{
    String FullName, Type, ImageUrl;

    ModelTeacherVerify()
    {

    }

    public ModelTeacherVerify(String fullName, String type, String imageUrl) {
        FullName = fullName;
        Type = type;
        ImageUrl = imageUrl;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
