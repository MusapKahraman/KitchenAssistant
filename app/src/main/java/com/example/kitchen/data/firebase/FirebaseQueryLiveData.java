/*
 * References:
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components_22.html
 */
package com.example.kitchen.data.firebase;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private final Handler handler = new Handler();
    private boolean listenerRemovePending = false;
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            listenerRemovePending = false;
        }
    };

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    FirebaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        } else {
            query.addListenerForSingleValueEvent(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        // Schedule removal of the listener after two seconds of delay.
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}