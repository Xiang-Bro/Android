package com.example.classdesign;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.classdesign.JavaBean.Goods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.image.SmartImageView;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class good_Detail extends AppCompatActivity {

    private static String who;//谁登录了

    private TextView number;
    private TextView describe_tv;
    private TextView price_tv;
    private TextView address_tv;
    private TextView sug_tv;
    private TextView buy_tv;
    private SmartImageView good_img;

    private String good_id;
    private String number_buy;

    List<Goods> list_lv = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.good_detail);

        number = findViewById(R.id.number);//购买数量
        good_img = findViewById(R.id.good_img);
        describe_tv = findViewById(R.id.describe);
        price_tv = findViewById(R.id.price);
        address_tv = findViewById(R.id.address);
        sug_tv = findViewById(R.id.sug_number);
        buy_tv = findViewById(R.id.buy_number);

        //接收商品跳转过来时的商品信息
        Intent intent = getIntent();
        good_id = intent.getStringExtra("good_id");
        who = intent.getStringExtra("who");
        System.out.println("商品界面的who ===" + who);
        System.out.println("接收的商品id为" + good_id);

        show();
    }

    public void show() {
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            //添加属性
            fb.add("what_do","detail");
            fb.add("id",good_id);
            FormBody body = fb.build();
            //拼接形成最终服务器的访问路径
            String link_address = null;

            link_address = Login.Address(this)+"about_goods";

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Goods>>(){}.getType();
                            list_lv = gson.fromJson(result,listType);
                            good_img.setImageUrl(list_lv.get(0).getImg());
                            describe_tv.setText(list_lv.get(0).getDescribe());
                            price_tv.setText("￥" + list_lv.get(0).getPrice());
                            address_tv.setText("发货地址：  " + list_lv.get(0).getAddress());
                            sug_tv.setText("推荐人数  " + list_lv.get(0).getNumber());
                            buy_tv.setText("销量  " + list_lv.get(0).getBuy());
                        }
                    });
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //立即购买 按钮监听
    public void now_buy(View v) {
        String num = number.getText().toString().trim();
        int buy_num = Integer.parseInt(num);
        String price[] = price_tv.getText().toString().trim().split("[￥]");
        int buy_price = Integer.parseInt(price[1]);
        Toast.makeText(this,String.valueOf(buy_num*buy_price),Toast.LENGTH_SHORT).show();
        to_insert();
        Intent intent = new Intent(this,Purchase.class);
        intent.putExtra("total_money",String.valueOf(buy_num*buy_price));
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //加入购物车
    public void Join(View v) {
        number_buy = number.getText().toString().trim();
        System.out.println("------准备添加传过来的who为" + who);
        if (who == null) {
            Toast.makeText(good_Detail.this, "请先登录或者注册用户", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(good_Detail.this,"加入成功",Toast.LENGTH_SHORT).show();
            try {
                //1.获取OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //2.构造RequestBody
                FormBody.Builder fb = new FormBody.Builder();
                //添加属性
                fb.add("what_do", "insert");
                fb.add("id", good_id);
                fb.add("user", who);
                fb.add("number", number_buy);
                //Toast.makeText(Good_detail.this, good_id + " === " + who + " ===" + number_buy, Toast.LENGTH_SHORT).show();
                FormBody body = fb.build();
                //拼接形成最终服务器的访问路径
                String link_address = null;

                link_address = Login.Address(this) + "about_car";

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //立即购买
    public void to_insert() {
        number_buy = number.getText().toString().trim();
        if (who == null) {
            Toast.makeText(good_Detail.this, "请先登录或者注册用户", Toast.LENGTH_SHORT).show();
        } else {
            try {
                //1.获取OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //2.构造RequestBody
                FormBody.Builder fb = new FormBody.Builder();
                //添加属性
                fb.add("what_do", "now_buy");
                fb.add("id", good_id);
                fb.add("user", who);
                fb.add("number", number_buy);
                //Toast.makeText(Good_detail.this, good_id + " === " + who + " ===" + number_buy, Toast.LENGTH_SHORT).show();
                FormBody body = fb.build();
                //拼接形成最终服务器的访问路径
                String link_address = null;

                link_address = Login.Address(this) + "about_buy";

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //数量增加和减少
    public void Little(View v) {
        int num = Integer.parseInt(number.getText().toString().trim());
        int new_number = num - 1;
        if(new_number < 1){
            Toast.makeText(this,"不能再减少了",Toast.LENGTH_SHORT).show();
        }else{
            number.setText(String.valueOf(new_number));
        }
    }
    public void More(View v) {
        int num = Integer.parseInt(number.getText().toString().trim());
        int new_number = num + 1;
        number.setText(String.valueOf(new_number));
    }
    //返回主页
    public void Back(View v) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }

    public static void zero_Detail() {
        who = null;
    }
}
