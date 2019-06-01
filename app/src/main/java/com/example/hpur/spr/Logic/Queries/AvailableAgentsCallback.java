package com.example.hpur.spr.Logic.Queries;

import com.example.hpur.spr.Logic.Models.AgentModel;
import java.util.ArrayList;

public interface AvailableAgentsCallback {
    void availableAgents(ArrayList<AgentModel> availableAgentsArray);
    void availableAgentsUID(ArrayList<String> availableAgentsUIDArray);
    void noAvailableAgentsUID();
}
