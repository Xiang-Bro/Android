package com.example.classdesign;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.classdesign.Utils.MD5Utils;
import com.example.classdesign.Utils.MySQLiteHelper;
import okhttp3.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

public class Login extends AppCompatActivity {

    private EditText user_et;
    private EditText mm_et;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        user_et = findViewById(R.id.username);
        mm_et = findViewById(R.id.password);
        //设置输入框中图标的大小
        Drawable drawable1 = getResources().getDrawable(R.mipmap.me);
        drawable1.setBounds(0,0,60,60);
        user_et.setCompoundDrawables(drawable1,null,null,null);
        Drawable drawable2 = getResources().getDrawable(R.mipmap.suo);
        drawable2.setBounds(0,0,60,60);
        mm_et.setCompoundDrawables(drawable2,null,null,null);
    }

    public void toChange(View v) {
        Intent intent = new Intent(this,toNew.class);
        String message;
        switch (v.getId()) {
            case R.id.forget:
                message = "忘记密码";
                //Toast.makeText(this,"忘记密码界面",Toast.LENGTH_SHORT).show();
                break;
            case R.id.register:
                message = "注册用户";
                //Toast.makeText(this,"注册用户界面",Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        intent.putExtra("message",message);
        startActivity(intent);
    }

    //登录
    public void Login(View v) throws Exception {
        final String phone = user_et.getText().toString().trim();
        final String password = mm_et.getText().toString().trim();

        //1.获取OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造RequestBody
        FormBody.Builder fb = new FormBody.Builder();
        fb.add("choose","登录");
        fb.add("phone",phone);
        fb.add("password", MD5Utils.stringToMD5(password));
        FormBody body = fb.build();
        //拼接形成最终服务器的访问路径
        String link_address = Address(this)+"about_user";
        //Toast.makeText(this,link_address,Toast.LENGTH_SHORT).show();
        //"http://192.168.1.152/Internet_data_war_exploded/about_user"
        Request request = new Request.Builder().url(link_address).post(body).build();

        //3.将Request封装为Call对象
        Call call = okHttpClient.newCall(request);
        //4.执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("", "onFailure: " + e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.e("", "onResponse: " + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equals("true")) {
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            intent.putExtra("user",phone);
                            startActivity(intent);
                            System.out.println("传给首页的who==" + phone);
                        }else {
                            Toast.makeText(Login.this,"账号或密码不正确",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //读取配置文件中的地址属性
    public static String Address(Context context) throws Exception {
        Properties properties = new Properties();
        InputStream is = context.getAssets().open("link");
        properties.load(is);
        String address = properties.getProperty("address");
        return address;
    }
}
