package com.example.hpur.spr.Logic;

import android.content.Context;

import com.example.hpur.spr.Logic.Types.GenderType;
import com.example.hpur.spr.Logic.Types.SectorType;
import com.example.hpur.spr.Logic.Types.SexType;
import com.example.hpur.spr.Storage.SharedPreferencesStorage;
import com.google.gson.Gson;

public class UserModel {
    private String email;
    private String nickname;
    private SexType sexType;
    private String living;
    private SectorType sectorType;
    private String birthday;
    private GenderType genderType;

    public UserModel() {
    }

    public UserModel(String email, String nickname, SexType sexType, String living, SectorType sectorType, String birthday, GenderType genderType) {
        this.email = email;
        this.nickname = nickname;
        this.sexType = sexType;
        this.living = living;
        this.sectorType = sectorType;
        this.birthday = birthday;
        this.genderType = genderType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public SexType getSexType() {
        return sexType;
    }

    public void setSexType(SexType sexType) {
        this.sexType = sexType;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public SectorType getSectorType() {
        return sectorType;
    }

    public void setSectorType(SectorType sectorType) {
        this.sectorType = sectorType;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

    public void saveLocalObj(Context ctx) {
        Gson gson = new Gson();
        String json = gson.toJson(this);

        SharedPreferencesStorage storage = new SharedPreferencesStorage(ctx);
        storage.saveData(json,"UserModel");
    }

    public UserModel readLocalObj(Context ctx) {
        Gson gson = new Gson();
        SharedPreferencesStorage storage = new SharedPreferencesStorage(ctx);

        String json = storage.readData("UserModel");
        UserModel obj = gson.fromJson(json, UserModel.class);

        return obj;
    }
}
