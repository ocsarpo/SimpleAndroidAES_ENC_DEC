package com.example.postw.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    private EditText et;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.editText);
        tv = findViewById(R.id.target);
    }

    public void backup(View view){
        String t = et.getText().toString();

        try {
            AES256Chiper.secretKey = "1234567890123456";
            String enc = AES256Chiper.AES_Encode(t);

            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File dir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyDirec");
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/MyDirec/Data.enc");
                file.createNewFile();

                FileWriter fw = new FileWriter(file, false);
                fw.write(enc);
                fw.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void recovery(View view){
        try{
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MyDirec/Data.enc";
            StringBuffer buffer = new StringBuffer();
            BufferedReader eReader = new BufferedReader(new FileReader(path));
            String data = eReader.readLine();
            while(data != null){
                buffer.append(data);
                data = eReader.readLine();
            }
            AES256Chiper.secretKey = "1234567890123456";
            String dec = AES256Chiper.AES_Decode(buffer.toString());
            tv.setText(dec);
            eReader.close();
        }catch (Exception  e){
            e.printStackTrace();
        }

    }

    // 권한 확인 후 대화상자 보여줌
    private void setPermission() {
        int w_permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int r_permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(w_permissionInfo == PackageManager.PERMISSION_GRANTED && r_permissionInfo == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(getApplicationContext(), "SDCard 읽기 쓰기 권한 있음", Toast.LENGTH_SHORT).show();
        else{
            // 재요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                Toast.makeText(getApplicationContext(), "권한의 필요성 설명", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                Toast.makeText(getApplicationContext(), "권한의 필요성 설명", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    // 해당 권한에 따른 사용자 수락여부 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;

        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "SDCard 읽쓰 권한 승인";
            else
                str = "SDCard 읽쓰 권한 거부";
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }
    }
}
