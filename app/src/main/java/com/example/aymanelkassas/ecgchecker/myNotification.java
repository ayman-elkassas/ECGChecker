package com.example.aymanelkassas.ecgchecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Ayman Elkassas on 2/1/2018.
 */

public class myNotification extends Service {

    static int flag=0;
    boolean isRunning=true;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient;
    ValueEventListener event,e1;

    FirebaseMessagingService ms;
    RemoteMessage.Notification s;

    static int counter=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");

        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning)
                {
                    try {
                        Thread.sleep(1000);
                        listenNotifiy();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void listenNotifiy()
    {
        if(mAuth.getUid()!=null)
        {
            event=mDoctor.child(mAuth.getUid()).child("notify").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final int count = (int) dataSnapshot.getChildrenCount();
                    for(DataSnapshot d:dataSnapshot.getChildren())
                    {
                        if(flag!=count)
                        {
                            flag++;
                            final String from=d.child("from").getValue(String.class);
                            final String message=d.child("message_content").getValue(String.class);
                            final String time=d.child("time").getValue(String.class);

                            e1=mPatient.child(from).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Bundle b=new Bundle();
                                    b.putString("message_content",message);
                                    b.putString("id",dataSnapshot.child("id").getValue(String.class));
                                    b.putString("fname",dataSnapshot.child("fname").getValue(String.class));
                                    b.putString("lname",dataSnapshot.child("lname").getValue(String.class));
                                    b.putString("job",dataSnapshot.child("job").getValue(String.class));
                                    b.putString("bio",dataSnapshot.child("bio").getValue(String.class));
                                    b.putString("email",dataSnapshot.child("email").getValue(String.class));
                                    b.putString("location",dataSnapshot.child("location").getValue(String.class));
                                    b.putString("phone",dataSnapshot.child("phone").getValue(String.class));
                                    b.putString("working_hours",dataSnapshot.child("working_hours").getValue(String.class));
                                    b.putString("avatar",dataSnapshot.child("avatar").getValue(String.class));

                                    Notification.Builder builder = new Notification.Builder(myNotification.this);
                                    builder.setContentTitle(dataSnapshot.child("fname").getValue(String.class)+" "+dataSnapshot.child("lname").getValue(String.class));
                                    builder.setContentText(message);
                                    builder.setSmallIcon(R.drawable.lifeline);
                                    Intent in = new Intent(getBaseContext(), report.class);
                                    in.putExtras(b);
                                    PendingIntent pIntent = PendingIntent.getActivity(myNotification.this, counter++,in,PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(pIntent);
                                    builder.setAutoCancel(true);
                                    builder.addAction(R.mipmap.ic_launcher, "Play", pIntent);
                                    builder.addAction(R.mipmap.ic_launcher, "pause", pIntent);

                                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    builder.setSound(alarmSound);

                                    Notification noti = builder.build();
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    manager.notify(counter++, noti);

                                    mPatient.child(from).removeEventListener(e1);
                                    mDoctor.child(mAuth.getUid()).child("notify").removeEventListener(event);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            mDoctor.child(mAuth.getUid()).child("notify").removeValue();
                            flag=0;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning=false;
        mAuth.signOut();
    }
}
