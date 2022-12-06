package com.fourdevs.diuquestionbnakadmin.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fourdevs.diuquestionbnakadmin.utilities.Constants;

import java.io.Serializable;
import java.util.Date;
@Entity(tableName = Constants.KEY_COLLECTION_CONTACTS)
public class Help implements Serializable {

    public String subject;
    public String message;
    public String dateTime;

    @PrimaryKey
    @NonNull
    public String userId = null;

    public Help() {

    }

    public Help getHelp() {
        Help help = new Help();
        help.subject = this.subject;
        help.message = this.message;
        help.dateTime = this.dateTime;
        help.userId = this.userId;
        return help;
    }


}
