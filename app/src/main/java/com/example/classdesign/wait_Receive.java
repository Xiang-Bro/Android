package com.example.classdesign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.classdesign.JavaBean.Car;
import com.example.classdesign.JavaBean.Goods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.image.SmartImageView;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class wait_Receive extends AppCompatActivity {

    private static String who;

    private ListView rec_lv;
    List<Car> rec_li = new ArrayList<>();
    private View receive_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_receive);

        receive_view = findViewById(R.id.wait_receive_view);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");

        rec_lv = findViewById(R.id.rec_lv);
        show_receive();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rec_li.size();
        }

        @Override
        public Object getItem(int position) {
            return rec_li.get(position);
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
            Button button;//确认收获
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.receive,parent,false);
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.img);
                holder.describe = convertView.findViewById(R.id.describe);
                holder.price = convertView.findViewById(R.id.price);
                holder.number = convertView.findViewById(R.id.number);
                holder.button = convertView.findViewById(R.id.sure_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setImageUrl(rec_li.get(position).getImg());
            holder.describe.setText(rec_li.get(position).getDescribe());
            holder.price.setText(rec_li.get(position).getPrice());
            holder.number.setText(rec_li.get(position).getNumber());

            //确认收货按钮监听
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击确认按钮之后弹出对话框确认收货之后，将该订单信息删除
                    AlertDialog dialog = new AlertDialog.Builder(wait_Receive.this)
                            .setMessage("确认收货？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //确认收货之后，将该订单信息删除
                                    toPingjia(rec_li.get(position).getId());
                                    System.out.println("确认收货的商品id为 === " + rec_li.get(position).getId());
                                    Intent intent = new Intent(wait_Receive.this,MainActivity.class);
                                    intent.putExtra("user",who);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消",null)
                            .create();
                    dialog.show();
                }
            });

            return convertView;
        }
    }

    //请求展示
    public void show_receive() {
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
                fb.add("what_do","show");
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
                                    Toast.makeText(wait_Receive.this,"没有相关订单",Toast.LENGTH_SHORT).show();
                                }
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Car>>(){}.getType();
                                rec_li = gson.fromJson(result,listType);

                                //ListView绑定监听器
                                MyAdapter myAdapter = new MyAdapter();
                                rec_lv.setAdapter(myAdapter);
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
    //确认收货之后将该订单从 待收货 表中删除，并将该条数据添加到待评价 表中
    public void toPingjia(String good_id) {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();

            fb.add("user",who);
            fb.add("good_id",good_id);
            fb.add("what_do","toPingjia");
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
