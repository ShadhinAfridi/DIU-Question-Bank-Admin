package com.fourdevs.diuquestionbnakadmin.models;

import java.io.Serializable;
import java.util.Date;

public class Course implements Serializable {
    public String departmentName, courseName, semester, year, fileUrl, exam, id, userId, dateTime;
    public Boolean approved;
    public Date dateObject;
}
