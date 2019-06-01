package com.example.hpur.spr.Logic.Queries;

public interface KnnCallback {
    void onKnnServiceRequestOnSuccess(String agentUid);
    void onKnnServiceRequestFailed();
}
