package com.example.teccompanion;

public class ModelTeacherVerify
{
    String FullName, Dept, ImageUrl;

    ModelTeacherVerify()
    {

    }


    public ModelTeacherVerify(String fullName, String dept, String imageUrl) {
        FullName = fullName;
        Dept = dept;
        ImageUrl = imageUrl;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getDept() {
        return Dept;
    }

    public void setDept(String dept) {
        Dept = dept;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
