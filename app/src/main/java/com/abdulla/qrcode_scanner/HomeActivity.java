package com.abdulla.qrcode_scanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class HomeActivity extends AppCompatActivity {
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button scnbtn = findViewById(R.id.scanbutton);
        scnbtn.setOnClickListener(v -> scanCode());
    }
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("zoom in or out to capture right");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlaucher.launch(options);

    }
    public boolean isOnline(){
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Error");
            builder.setMessage("can't connect to internet :_(");
            builder.setPositiveButton("OK", (dialog, which) -> counter = 0).show();        }
        return true;
    }


    ActivityResultLauncher<ScanOptions> barlaucher = registerForActivityResult(new ScanContract(), result ->
    {
        isOnline();
        String guest = result.getContents();
        if (guest != null){
            DatabaseReference storage = FirebaseDatabase.getInstance().getReference().child("1sMrJpKvTBoPlGKJHMXjo7wiOpqGT1equ8yxV4JNBx-8").child("Sheet1").child(guest);
//          FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//          storage.keepSynced(true);
            storage.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Information info = snapshot.getValue(Information.class);
                        if(info.getAttendence().equals("Not Yet")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle(guest);
                            builder.setMessage("Situation: Welcome :)");
                            counter++;
                            builder.setPositiveButton("Done", (dialog, which) -> {
                                scanCode();
                                FirebaseDatabase.getInstance().getReference().child("1sMrJpKvTBoPlGKJHMXjo7wiOpqGT1equ8yxV4JNBx-8").child("Sheet1").child(guest).child("Attendence").setValue("Yes");
                            }).show();
                        } else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle(guest);
                            builder.setMessage("Situation" + "\n" +"scanned before :(");
                            counter++;
                            builder.setPositiveButton("Done", (dialog, which) -> scanCode()).show();
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle(guest);
                        builder.setMessage("Imposter" + "\n" +"not even in database");
                        counter++;
                        builder.setPositiveButton("Done", (dialog, which) -> scanCode()).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("can't connect to database :(");
                    builder.setPositiveButton("OK", (dialog, which) -> scanCode()).show();
                }
            });
        }
    });
}
