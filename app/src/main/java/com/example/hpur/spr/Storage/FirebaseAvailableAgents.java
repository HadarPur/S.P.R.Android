package com.example.hpur.spr.Storage;

import android.content.Context;
import com.example.hpur.spr.Logic.Models.AgentModel;
import com.example.hpur.spr.Logic.Queries.AvailableAgentsCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;

public class FirebaseAvailableAgents {
    private DatabaseReference mRefAvailableAgents;
    private DatabaseReference mRefAvailableAgentsUID;

    // c'tor
    public FirebaseAvailableAgents() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRefAvailableAgentsUID = data.getReference("SPRApp").child("Available Agents");
        this.mRefAvailableAgents = data.getReference("SPRApp").child("Agents");

    }

    public void readAvailableAgentsUID(final Context ctx, final AvailableAgentsCallback queryCallback) {
        this.mRefAvailableAgentsUID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                ArrayList<String> agentsUID = new ArrayList<>();
                while(itr.hasNext()) {
                    agentsUID.add(itr.next().getKey());
                }
                queryCallback.availableAgentsUID(agentsUID);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                queryCallback.noAvailableAgentsUID();
            }
        });
    }

    public void readAvailableAgents(final Context ctx, final ArrayList<String> agentsUIDArray, final AvailableAgentsCallback queryCallback) {
        this.mRefAvailableAgents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                ArrayList<AgentModel> agents = new ArrayList<>();
                while(itr.hasNext()) {
                    DataSnapshot obj = itr.next();
                    if (agentsUIDArray.contains(obj.getKey())) {
                        AgentModel agent = obj.getValue(AgentModel.class);
                        agent.setAgentUID(obj.getKey());
                        agents.add(agent);
                    }
                }
                queryCallback.availableAgents(agents);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
