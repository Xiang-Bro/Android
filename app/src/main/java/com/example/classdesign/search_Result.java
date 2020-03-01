package com.example.classdesign;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

public class search_Result extends AppCompatActivity {

    private static String who;
    private String option;
    private EditText search;
    private ListView find_lv;
    private List<Goods> find_li = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        option = intent.getStringExtra("option");

        search = findViewById(R.id.search);
        search.setText(option);
        Drawable drawable = getResources().getDrawable(R.mipmap.search);
        drawable.setBounds(0,0,60,60);
        search.setCompoundDrawables(drawable,null,null,null);

        find_lv = findViewById(R.id.lv);
        show();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return find_li.size();
        }

        @Override
        public Object getItem(int position) {
            return find_li.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            SmartImageView img;//商品图片
            TextView des_tv;
            TextView price_tv;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.goods,parent,false);
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.img);
                holder.des_tv = convertView.findViewById(R.id.describe);
                holder.price_tv = convertView.findViewById(R.id.price);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.setImageUrl(find_li.get(position).getImg());
            holder.des_tv.setText(find_li.get(position).getDescribe());
            holder.price_tv.setText("￥" + find_li.get(position).getPrice());
            return convertView;
        }
    }

    //展示查询结果
    public void show() {
        //从服务器查找商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("option",option);
            fb.add("what_do","search");
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
                            find_li = gson.fromJson(result,listType);

                            //ListView绑定监听器
                            MyAdapter myAdapter = new MyAdapter();
                            find_lv.setAdapter(myAdapter);

                            //ListView设置监听器
                            //获取点击的item商品的id，跳转至商品详情页
                            find_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String good_id = find_li.get(position).getId();
                                    Intent intent = new Intent(search_Result.this, good_Detail.class);
                                    intent.putExtra("good_id",good_id);
                                    intent.putExtra("who",who);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View v) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
}
