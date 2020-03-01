package com.example.classdesign;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.example.classdesign.Utils.MD5Utils;
import com.example.classdesign.Utils.MySQLiteHelper;
import okhttp3.*;

import java.io.IOException;
import java.util.Random;

public class toNew extends AppCompatActivity {

    private TextView tv;//标题
    private ImageView iv;//标题图片
    private EditText account_et;//输入账户
    private EditText password_et;//输入密码
    private EditText re_password_et;//确认密码
    private EditText sure_et;//输入验证码
    private Button send_btn;//发送验证码
    private Button login_btn;//确认提交

    String user;//用户框
    String password;//密码框
    String re_password;//确认密码框
    String sure_code;//验证码框
    String sure;//发送的验证码
    String submit_btn;//提交按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_new);

        //请求获取短信权限
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS} , 1);

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.img);
        account_et = findViewById(R.id.username);
        password_et = findViewById(R.id.password);
        re_password_et = findViewById(R.id.re_password);
        sure_et = findViewById(R.id.sure);
        send_btn = findViewById(R.id.send);
        login_btn = findViewById(R.id.submit);


        //设置输入框中图标的大小
        Drawable account_draw = getResources().getDrawable(R.mipmap.me);
        account_draw.setBounds(0, 0, 60, 60);
        account_et.setCompoundDrawables(account_draw, null, null, null);
        Drawable password_draw = getResources().getDrawable(R.mipmap.suo);
        password_draw.setBounds(0, 0, 60, 60);
        password_et.setCompoundDrawables(password_draw, null, null, null);
        Drawable re_password_draw = getResources().getDrawable(R.mipmap.suo);
        re_password_draw.setBounds(0, 0, 60, 60);
        re_password_et.setCompoundDrawables(re_password_draw, null, null, null);

        //接收页面跳转传送的数据
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if (message.equals("忘记密码")) {
            tv.setText("修改密码");
            iv.setImageResource(R.mipmap.change);
            login_btn.setText("确认修改");
            //Toast.makeText(this, "忘记密码界面  " , Toast.LENGTH_SHORT).show();
        }
        else {
            tv.setText("注册用户");
            iv.setImageResource(R.mipmap.user_new);
            account_et.setHint("请输入手机号");
            login_btn.setText("立即注册");
        }
    }


    //提交时判断
    public void Submit(View view) throws Exception {
        //获取输入的数据
        user = account_et.getText().toString().trim();
        password = password_et.getText().toString().trim();
        re_password = re_password_et.getText().toString().trim();
        sure_code = sure_et.getText().toString().trim();
        submit_btn = login_btn.getText().toString().trim();

        if(password.length() < 8 || password.length() > 16)
            Toast.makeText(this,"密码长度为8到16位",Toast.LENGTH_SHORT).show();

        if(!password.equals(re_password)){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_SHORT).show();
        }
        //验证验证码
        if(!sure_code.equals(sure)){
            Toast.makeText(this,"验证码错误",Toast.LENGTH_SHORT).show();
        }

        //1.获取OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造RequestBody
        FormBody.Builder fb = new FormBody.Builder();
        fb.add("choose",submit_btn);
        fb.add("phone",user);
        fb.add("password", MD5Utils.stringToMD5(password));
        FormBody body = fb.build();
        //拼接形成最终服务器的访问路径
        String link_address = Login.Address(this) +"about_user";
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
                            Intent intent = new Intent(toNew.this,Login.class);
                            startActivity(intent);
                        }else {
                            if(submit_btn.equals("确认修改"))
                                Toast.makeText(toNew.this,"账号不存在",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(toNew.this,"账号已存在",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //发送验证码
    public void send(View v) {
        user = account_et.getText().toString().trim();
        if(user == null) {
            Toast.makeText(this,"请输入账号",Toast.LENGTH_SHORT).show();
        } else {
            //倒计时
            MyCountDownTimer timer = new MyCountDownTimer(60000, 1000);
            timer.start();
            Random rd = new Random();
            sure = null;
            int a;
            for(int i = 0; i < 6; i++){
                a = rd.nextInt(10);
                if(i ==0)
                    sure = String.valueOf(a);
                else
                    sure += String.valueOf(a);
            }
            //sendSMS(user,"祥子货铺发送验证码：" + sure);
            String message = "祥子货铺发送验证码：" + sure;
            System.out.println("祥子货铺给" + user + "发送验证码：" + sure);
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

            //调用接口发送短信
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent pi = PendingIntent.getBroadcast(toNew.this, 0, new Intent(), 0);
            smsManager.sendTextMessage(user, null, message, pi, null);
        }
    }

    //倒计时效果
    private class MyCountDownTimer extends CountDownTimer {

        //millisInFuture：总时间  countDownInterval：每隔多少时间刷新一次
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        public void onTick(long lm) {
            //不允许再次点击
            send_btn.setClickable(false);
            send_btn.setText(lm / 1000 + "秒后可重新发送");
        }

        //计时结束
        @Override
        public void onFinish() {
            send_btn.setClickable(true);
            send_btn.setText("重新获取");
        }
    }
}
