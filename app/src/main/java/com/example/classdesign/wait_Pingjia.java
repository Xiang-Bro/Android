package com.example.classdesign;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.classdesign.JavaBean.Car;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.image.SmartImageView;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class wait_Pingjia extends AppCompatActivity {

    private static String  who;
    private List<Car> ping_li = new ArrayList<>();
    private ListView ping_lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_pingjia);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");

        ping_lv = findViewById(R.id.ping_lv);
        show();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ping_li.size();
        }

        @Override
        public Object getItem(int position) {
            return ping_li.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            SmartImageView img;
            TextView describe_tv;
            TextView price_tv;
            TextView number_tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.order, parent, false);
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.img);
                holder.describe_tv = convertView.findViewById(R.id.describe);
                holder.price_tv = convertView.findViewById(R.id.price);
                holder.number_tv = convertView.findViewById(R.id.number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setImageUrl(ping_li.get(position).getImg());
            holder.describe_tv.setText(ping_li.get(position).getDescribe());
            holder.price_tv.setText(ping_li.get(position).getPrice());
            holder.number_tv.setText(ping_li.get(position).getNumber());

            return convertView;
        }
    }

    public void show() {
        //从服务器获取推荐的商品
        if(who == null) {
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                //1.获取OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //2.构造RequestBody
                FormBody.Builder fb = new FormBody.Builder();
                fb.add("user", who);
                fb.add("what_do", "show_ping");
                FormBody body = fb.build();
                //拼接形成最终服务器的访问路径
                String link_address = null;

                link_address = Login.Address(this) + "about_order";

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
                                if (result == null) {
                                    Toast.makeText(wait_Pingjia.this, "没有相关订单", Toast.LENGTH_SHORT).show();
                                }
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Car>>() {
                                }.getType();
                                ping_li = gson.fromJson(result, listType);

                                //ListView绑定监听器
                                MyAdapter myAdapter = new MyAdapter();
                                ping_lv.setAdapter(myAdapter);

                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
