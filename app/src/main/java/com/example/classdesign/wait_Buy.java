package com.example.classdesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
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

public class wait_Buy extends AppCompatActivity {

    private List<Car> buy_li = new ArrayList<>();
    private static String who;
    private ListView buy_lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_buy);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        buy_lv = findViewById(R.id.buy_lv);
        show_buy();

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return buy_li.size();
        }

        @Override
        public Object getItem(int position) {
            return buy_li.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            SmartImageView img;
            TextView describe;
            TextView number;
            TextView price;
            Button btn1;
            Button btn2;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.buy,parent,false);
                holder.img = convertView.findViewById(R.id.img);
                holder.describe = convertView.findViewById(R.id.describe);
                holder.number = convertView.findViewById(R.id.number);
                holder.price = convertView.findViewById(R.id.price);
                holder.btn1 = convertView.findViewById(R.id.cancel);
                holder.btn2 = convertView.findViewById(R.id.now_buy);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.img.setImageUrl(buy_li.get(position).getImg());
            holder.describe.setText(buy_li.get(position).getDescribe());
            holder.price.setText(buy_li.get(position).getPrice());
            holder.number.setText(buy_li.get(position).getNumber());

            //取消订单 按钮监听事件
            holder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog;
                    dialog = new AlertDialog.Builder(wait_Buy.this)
                            .setMessage("确认取消？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handle("cancel",buy_li.get(position).getId());
                                    Intent intent = new Intent(wait_Buy.this,MainActivity.class);
                                    intent.putExtra("user",who);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    dialog.show();
                }
            });
            //立即支付 按钮监听事件
            holder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog;
                    dialog = new AlertDialog.Builder(wait_Buy.this)
                            .setMessage("确认支付？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handle("now_buy",buy_li.get(position).getId());
                                    Intent intent = new Intent(wait_Buy.this,MainActivity.class);
                                    intent.putExtra("user",who);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    dialog.show();
                }
            });
            return convertView;
        }
    }

    public void show_buy() {
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
                fb.add("what_do","show_buy");
                fb.add("user",who);
                FormBody body = fb.build();
                //拼接形成最终服务器的访问路径
                String link_address = null;

                link_address = Login.Address(this)+"about_order";

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
                                if(result == null){
                                    Toast.makeText(wait_Buy.this,"没有相关订单",Toast.LENGTH_SHORT).show();
                                }
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Car>>(){}.getType();
                                buy_li = gson.fromJson(result,listType);

                                //ListView绑定监听器
                                MyAdapter myAdapter = new MyAdapter();
                                buy_lv.setAdapter(myAdapter);
                            }
                        });
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //订单处理
    public void handle(String what_do, String id) {
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
                fb.add("what_do",what_do);
                fb.add("good_id",id);
                fb.add("user",who);
                FormBody body = fb.build();
                //拼接形成最终服务器的访问路径
                String link_address = null;

                link_address = Login.Address(this)+"about_order";

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
}
