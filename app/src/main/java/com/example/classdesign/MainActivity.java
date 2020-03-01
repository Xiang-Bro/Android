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

/*
* address = http://192.168.1.153:8080/Internet_data_war_exploded/    机房
            http://10.153.77.128:8080/Internet_data_war_exploded/    宿舍
            http://192.168.43.89:8080/Internet_data_war_exploded/    热点
*/
public class MainActivity extends AppCompatActivity {


    private static String who;//谁登录了

    private EditText et1;
    private ListView lv;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private Button btn;

    List<Goods> list_lv = new ArrayList<>();//接收数据集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv1 = findViewById(R.id.main);
        iv2 = findViewById(R.id.shop);
        iv3 = findViewById(R.id.me);
        btn = findViewById(R.id.find);

        //接收登录者的id 从始至终带着
        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        System.out.println("从登陆界面跳转过来的who === " + who);
        System.out.println("主页的who === " + who);

        lv = findViewById(R.id.lv);

        //设置搜索框中图标的大小
        et1 = findViewById(R.id.search);
        Drawable drawable = getResources().getDrawable(R.mipmap.search);
        drawable.setBounds(0,0,60,60);
        et1.setCompoundDrawables(drawable,null,null,null);

        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            //3.向服务器请求的参数 POST方式
            fb.add("what_do","main");
            FormBody body = fb.build();
            //拼接形成访问路径
            String link_address = null;
            link_address = Login.Address(this)+"about_goods";
            //4.向请求路径发出请求，传递参数
            Request request = new Request.Builder().url(link_address).post(body).build();
            //5.将Request封装为Call对象
            Call call = okHttpClient.newCall(request);
            //6.执行Call
            call.enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    Log.e("", "onFailure: " + e);
                }
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    Log.e("", "onResponse: " + result);
                    System.out.println(result);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Goods>>(){}.getType();
                            list_lv = gson.fromJson(result,listType);

                            //ListView绑定监听器
                            MyAdapter myAdapter = new MyAdapter();
                            lv.setAdapter(myAdapter);
                            //ListView设置监听器
                            //获取点击的item商品的id，跳转至商品详情页
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String good_id = list_lv.get(position).getId();
                                    Intent intent = new Intent(MainActivity.this, good_Detail.class);
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

    //设置ListView信息
    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return list_lv.size();
        }

        public Object getItem(int position) {
            return list_lv.get(position);
        }

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
            holder.img.setImageUrl(list_lv.get(position).getImg());
            holder.des_tv.setText(list_lv.get(position).getDescribe());
            holder.price_tv.setText("￥" + list_lv.get(position).getPrice());
            return convertView;
        }
    }

    //跳转至查找结果界面
    public void toFind(View v) {
        Intent intent = new Intent(this,search_Result.class);
        String option = et1.getText().toString().trim();
        intent.putExtra("option",option);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //跳转至分类界面
    public void toCategory(View view) {
        Intent intent = new Intent(this,Category.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //判断当前界面是否处于运行状态，设置对应的导航图标
    protected void onResume() {
        iv1.setImageResource(R.mipmap.home_after);
        iv1.getLayoutParams().height = 120;
        super.onResume();
    }
    //跳转至 购物车 界面
    public void Car(View v) {
        Intent intent = new Intent(this,Shop_car.class);
        intent.putExtra("user",who);
        System.out.println("主页给购物车界面的who === " + who);
        startActivity(intent);
    }
    //跳转至 我的 界面
    public void Me(View v){
        Intent intent = new Intent(this,Mine.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }

    public static void zero_Main() {
        who = null;
    }

}
