package com.example.hpur.spr.Logic.Models;

import android.content.Context;
import com.example.hpur.spr.Logic.Queries.AvailableAgentsCallback;
import com.example.hpur.spr.Logic.Types.GenderType;
import com.example.hpur.spr.Logic.Types.SectorType;
import com.example.hpur.spr.Logic.Types.SexType;
import com.example.hpur.spr.Storage.FirebaseAvailableAgents;
import java.util.ArrayList;

public class AgentModel {

    private String email;
    private String firstName;
    private String lastName;
    private SexType sexType;
    private String living;
    private SectorType sectorType;
    private String birthday;
    private GenderType genderType;

    private ArrayList<String> mAgentsUIDArray;
    private ArrayList<AgentModel> mAgentsArray;
    private String mUID;

    public AgentModel() {
    }

    public AgentModel(String email, String firstName, String lastName, SexType sexType, String living, SectorType sectorType, String birthday, GenderType genderType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void getAvailableAgentsUID(Context ctx, AvailableAgentsCallback callback) {
        FirebaseAvailableAgents agents = new FirebaseAvailableAgents();
        agents.readAvailableAgentsUID(ctx, callback);
    }

    public void getAvailableAgents(Context ctx, ArrayList<String> agentsUIDArray, AvailableAgentsCallback callback) {
        FirebaseAvailableAgents agents = new FirebaseAvailableAgents();
        agents.readAvailableAgents(ctx, agentsUIDArray, callback);
    }

    public void setAvailableAgentsArrayUID(ArrayList<String> agentsUIDArray) {
        this.mAgentsUIDArray = agentsUIDArray;
    }

    public ArrayList<String> getAvailableAgentsArrayUID() {
        return mAgentsUIDArray;
    }

    public void setAvailableAgentsArray(ArrayList<AgentModel> agentsArray) {
        this.mAgentsArray = agentsArray;
    }

    public ArrayList<AgentModel> getAvailableAgentsArray() {
        return mAgentsArray;
    }

    public void setAgentUID (String uid) {
        this.mUID = uid;
    }

    public String getAgentUID() {
        return mUID;
    }
}
