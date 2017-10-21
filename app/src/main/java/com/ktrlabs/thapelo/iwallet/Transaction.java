package com.ktrlabs.thapelo.iwallet;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Thapelo on 2017/10/09.
 */

public class Transaction extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;
    private String type,amount,fee,status;
    private  Long date;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getFee() {
        return fee;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {return status;}
}
