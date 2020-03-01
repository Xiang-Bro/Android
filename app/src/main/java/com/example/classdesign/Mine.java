package com.example.classdesign;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.*;

import java.io.IOException;

public class Mine extends AppCompatActivity {

    private static String who;
    private ImageView iv3;
    private TextView login_tv;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        System.out.println("我的界面的who === " + who);
        iv3 = findViewById(R.id.me);
        login_tv = findViewById(R.id.login);
    }


    //跳转至 登录 界面
    public void Login(View v) {
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
        login_tv.setText("切换账户");
    }
    //判断当前界面是否处于运行状态，设置对应的导航图标
    protected void onResume() {
        iv3.setImageResource(R.mipmap.me_after);
        iv3.getLayoutParams().height = 120;
        super.onResume();
    }
    //跳转至 主页 界面
    public void Main(View v){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //跳转至 购物车 界面
    public void Car(View v) {
        Intent intent = new Intent(this,Shop_car.class);
        intent.putExtra("user",who);
        System.out.println("我的界面传给购物车界面的who === " + who);
        startActivity(intent);
    }
    //跳转至 待收货 界面
    public void show_receive(View v) {
        Intent intent = new Intent(this,wait_Receive.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //跳转至 待付款 界面
    public void show_buy(View v) {
        Intent intent = new Intent(this, wait_Buy.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //跳转至 待评价 界面
    public void show_pingjia(View v) {
        Intent intent = new Intent(this,wait_Pingjia.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //注销登录  注销时清除订单表
    public void byeBye(View v) {
        if(who == null) {
            Toast.makeText(this,"您还没有登录",Toast.LENGTH_SHORT).show();
        } else {
            clear();
            MainActivity.zero_Main();
            good_Detail.zero_Detail();
            Shop_car.zero_Car();
            who = null;
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            login_tv.setText("点我登录");
        }
    }
    public void clear() {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("what_do","clear");
            fb.add("user",who);
            FormBody body = fb.build();
            //拼接形成最终服务器的访问路径
            String link_address = null;

            link_address = Login.Address(this)+"about_buy";

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
                    System.out.println(result);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
