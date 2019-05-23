package com.example.hpur.spr.Logic;

import com.example.hpur.spr.Logic.Models.ShelterModel;
import com.example.hpur.spr.Storage.FireBaseCitiesData;
import java.util.ArrayList;

public class ShelterInstance {
    private static ShelterInstance instance;

    //singleton
    public static ShelterInstance getInstance() {
        if (instance == null) {
            instance = new ShelterInstance();
        }
        return instance;
    }

    private static final String TAG = "CALL";

    ArrayList<ShelterModel> mData[]; /** each index in the list refers to shelters for each city in israel. **/

    //c'tor
    private ShelterInstance(){
    }

    //init array
    public void initAll(int length){
        this.mData = new ArrayList[length];
        for(int i = 0 ; i < this.mData.length ; i++){
            this.mData[i] = new ArrayList<>();
        }
    }

    //return shelters on specific city
    public ArrayList<ShelterModel> getShelters(int index) {
        return this.mData[index];
    }

    //gets all mData
    public ArrayList<ShelterModel>[] getData() {
        return this.mData;
    }

    public int arrSize(){
        return this.mData.length;
    }

    //interface for the callback
    public interface Callback {
        void onCallback(ArrayList<ShelterModel>[] cloudData);
    }

    //reading mData from the cloud
    public void readData(Callback callback, String[] cities) {
        FireBaseCitiesData data = new FireBaseCitiesData(cities);
        data.ReadData(callback);
    }

    //set the mData that read on the array
    public void setData(ArrayList<ShelterModel>[] arr) {
        initAll(arr.length);
        for(int i = 0 ; i < arr.length ; i++){
            this.mData[i].addAll(arr[i]);
        }
    }
 }
