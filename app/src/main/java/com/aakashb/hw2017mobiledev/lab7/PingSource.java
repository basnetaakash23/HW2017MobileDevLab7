package com.aakashb.hw2017mobiledev.lab7;

/**
 * Created by aakashb on 8/7/17.
 */


import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PingSource {

    public interface PingListener {
        void onPingsReceived(List<Ping> pingList);
    }

    private static PingSource sNewsSource;

    private Context mContext;
    private DatabaseReference pingsRef;

    public static PingSource get(Context context) {
        if (sNewsSource == null) {
            sNewsSource = new PingSource(context);
        }
        return sNewsSource;
    }

    private PingSource(Context context) {
        mContext = context;
    }

    // Firebase methods for you to implement.

    public void getPings(final PingListener pingListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pingsRef = databaseReference.child("pings");
        Query last50PingsQuery = pingsRef.limitToLast(50);
        last50PingsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ping> list_of_pings = new Vector<Ping>();
                Iterable<DataSnapshot> pingSnapshots = dataSnapshot.getChildren();
                Iterator<DataSnapshot> child = pingSnapshots.iterator();
                while(child.hasNext()){
                    Ping ping = new Ping(child.next());
                    list_of_pings.add(ping);

                }
                pingListener.onPingsReceived(list_of_pings);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void getPingsForUserId(String userId, final PingListener pingListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pingsRef = databaseReference.child("pings");
        Query userQuery = pingsRef.orderByChild("userId").equalTo(userId).limitToLast(50);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ping> list = new Vector<Ping>();
                Iterable<DataSnapshot> pingSnapshots = dataSnapshot.getChildren();
                Iterator<DataSnapshot> child = pingSnapshots.iterator();
                while (child.hasNext()){
                    Ping ping = new Ping(child.next());
                    list.add(ping);
                }
                pingListener.onPingsReceived(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendPing(final Ping ping) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pingsRef = databaseReference.child("pings");
        DatabaseReference newPingRef = pingsRef.push();
        Map<String, Object> pingValMap = new HashMap<String, Object>() {{
            put ("userName", ping.getUserName());
            put("userId", ping.getUserId());
            put("timestamp", ServerValue.TIMESTAMP);}
        };
        newPingRef.setValue(pingValMap);

    }
}

