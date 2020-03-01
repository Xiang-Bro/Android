package com.example.classdesign;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

public class Shop_car extends AppCompatActivity {

    boolean flag = true;//判断全选按钮是否为自己触发
    boolean[] checks = null;//商品的选中情况
    private ImageView iv2;//购物车图标
    private ListView car_lv;
    private CheckBox all;//全选
    private TextView money_tv;//总价钱

    double total_price = 0;

    private static String who;//谁登录了

    List<Car> car_list = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_car);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        System.out.println("购物车界面的who === " + who);
        showCar();

        iv2 = findViewById(R.id.shop);//图标
        money_tv = findViewById(R.id.money);//总价钱
        all = findViewById(R.id.all);//全选
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            System.out.println("购物车中的订单数量 === " + car_list.size());
            return car_list.size();
        }

        @Override
        public Object getItem(int position) {
            return car_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //ListView所要展示的信息属性
        class ViewHolder {
            SmartImageView img;
            TextView des;
            TextView price;
            CheckBox choose;
            TextView number;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.car, parent, false);
                holder = new ViewHolder();
                holder.des = convertView.findViewById(R.id.describe);
                holder.img = convertView.findViewById(R.id.img);
                holder.price = convertView.findViewById(R.id.price);
                holder.choose = convertView.findViewById(R.id.choose);
                holder.number = convertView.findViewById(R.id.number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final int pos = position; //pos必须声明为final

            //根据选中状态获取当前总价钱
            holder.choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checks[pos] = isChecked;
                    int number = 0;

                    for (int i = 0; i < checks.length; i++) {
                        if (checks[i]) {
                            number++;
                            double money_one = Double.valueOf(car_list.get(i).getPrice());
                            int buy_number = Integer.parseInt(car_list.get(i).getNumber());
                            double now_money = money_one*buy_number;
                            total_price += now_money;
                        }
                    }
                    money_tv.setText("合计: ￥" + String.format("%.2f", total_price));
                    if (number == checks.length) {
                        flag = true;
                        all.setChecked(true);
                    } else {
                        flag = false;
                        all.setChecked(false);
                    }

                    inOrOut(isChecked,position);
                }
            });

            holder.img.setImageUrl(car_list.get(position).getImg());
            holder.des.setText(car_list.get(position).getDescribe());
            holder.number.setText("x" + car_list.get(position).getNumber());
            holder.price.setText("￥" + car_list.get(position).getPrice());
            holder.choose.setChecked(checks[pos]);
            return convertView;
        }
    }

    //展示登录人的 购物车 信息
    public void showCar() {
        //从服务器获取推荐的商品
        try {
                //1.获取OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //2.构造RequestBody
                FormBody.Builder fb = new FormBody.Builder();
                fb.add("what_do", "show");
                fb.add("user", who);
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
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Car>>() {}.getType();
                                car_list = gson.fromJson(result, listType);
                                System.out.println("查询购物车订单生成的集合" + car_list);

                                //给ListView设置监听器
                                car_lv = findViewById(R.id.car_lv);
                                MyAdapter myAdapter = new MyAdapter();
                                car_lv.setAdapter(myAdapter);

                                checks = new boolean[car_list.size()];
                                all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            for (int i = 0; i < checks.length; i++) {
                                                checks[i] = true;
                                            }
                                            final int count = car_lv.getChildCount();
                                            for (int i = 0; i < count; i++) {
                                                final LinearLayout content = (LinearLayout) car_lv.getChildAt(i);
                                                CheckBox temp = content.findViewById(R.id.choose);
                                                temp.setChecked(true);
                                            }
                                        } else {
                                            if (flag) {
                                                for (int i = 0; i < checks.length; i++) {
                                                    checks[i] = false;
                                                }
                                                final int count = car_lv.getChildCount();
                                                for (int i = 0; i < count; i++) {
                                                    final LinearLayout content = (LinearLayout) car_lv.getChildAt(i);
                                                    CheckBox temp = content.findViewById(R.id.choose);
                                                    temp.setChecked(false);
                                                }
                                            }
                                        }
                                    }
                                });

                                car_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                        Intent intent = new Intent(Shop_car.this, good_Detail.class);
                                        intent.putExtra("good_id", car_list.get(position).getId());
                                        intent.putExtra("user", who);
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
    //判断每一商品是否选择，实现待购买订单的增删
    public void inOrOut(Boolean isChecked,int position) {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            if(isChecked){
                fb.add("what_do","plan_buy");
                fb.add("number",car_list.get(position).getNumber());
            } else {
                fb.add("what_do","plan_delete");
            }
            fb.add("id",car_list.get(position).getId());
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
    //支付选中的商品
    public void Purchase(View v) {
        if(who == null) {
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
        } else if(money_tv.getText().toString().trim().equals("合计： ￥0")) {
            Toast.makeText(this,"请选择要购买的商品",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this, Purchase.class);
            intent.putExtra("user", who);
            intent.putExtra("total_money", total_price + "");
            startActivity(intent);
        }
    }
    //判断当前界面是否处于运行状态，设置对应的导航图标
    protected void onResume() {
        iv2.setImageResource(R.mipmap.shopping_after);
        iv2.getLayoutParams().height = 120;
        super.onResume();
    }
    //跳转至 主页 界面
    public void Main(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }
    //跳转至 我的 界面
    public void Me(View v) {
        Intent intent = new Intent(this, Mine.class);
        intent.putExtra("user",who);
        startActivity(intent);
    }

    public static void zero_Car() {
        who = null;
    }
}