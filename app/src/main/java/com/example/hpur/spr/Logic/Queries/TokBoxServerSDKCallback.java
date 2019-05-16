package com.example.hpur.spr.Logic.Queries;

public interface TokBoxServerSDKCallback {
    void onTokboxRequestSucceed(String apiKey, String sessionId, String tokenPublisher, String tokenSubscriber);
    void onTokboxRequestFailed();
}
