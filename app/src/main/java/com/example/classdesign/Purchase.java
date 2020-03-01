package com.example.classdesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.classdesign.JavaBean.Car;
import com.example.classdesign.alipay.AuthResult;
import com.example.classdesign.alipay.PayDemoActivity;
import com.example.classdesign.alipay.PayResult;
import com.example.classdesign.alipay.util.OrderInfoUtil2_0;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.image.SmartImageView;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//订单界面
public class Purchase extends AppCompatActivity {

    private static String who;

    private TextView who_tv;
    private List<Car> order_list = new ArrayList();
    private ListView order_lv;

    private TextView money_tv;
    private TextView num_tv;
    private String total_money;//总价钱
    private int total_number;//总数量

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016101500688493";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088102179613684";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "nkqxnf0903@sandbox.com";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCp7wG3jJtcM5291KJsP2NCjnc/6Pg3BLVM4hLr26M2Hx+Jk+tOGz4R7w8baAdHIDhPIDsApjwNmPHuAYMe5KJZXosXjn5ayTi2PSlfwr5/yANqYL3ACK+JOx7q5qtgShbjeC7uP1zzHFYOU+s9UFdf7DH2bwMhXABjjKiUCRcQmxI+77KtXDeMcyaCzdAEDZ52te8lfQSCuEbMXMLuOoqHLgjAH+0XPROPGNonVWVkOhITI+UPMwmLdZ4ZDXD+odL5d3iRLryc6dKFpSfB/ZkjjVZySDFWEqyu7NyPO6Ts8CSF0rGTV+uCJA+PvLh98Dos5YVxLSlBXX4WdN1kZkOpAgMBAAECggEALAfexQVst+4z4Bz5XNzrxjIN2gRuVCsnIt2tE2ncl0hnLomiPaG3aoQrwOkqgZZqoK30O83W35sSjJ3dsKKDIm7p0Ve9i5550FZ2ovZdz9QudmDMqbZWbVNhRnIwU4m+9n+MgXTvi5p6/clmuYNYsGUN6RGImRsyipjGkVztmgJ2137V8AB154wi8TBPwdBtuW3N/OYjVUFO98cmfoZMVBUINP++x5AAuyze0JBJtNnN8NC386FqsqOIXSQbcCrYfdtjDkvQPOZ6GoUoFZMK3kcHvh0akrNHo0+DNqm4eoicb7HWznh4PbahJj+NRDw/wHReSx19HlYkl5+uChsUZQKBgQDy3Eh6Jbrxy/cVYRnmGTQEeNEhpNvylppir9Uldvksb9ZRf1IziKNF9Skijxx1FYPscu8cSspAjSo1k69AV+R7R1JpRCvqUb+tx/6sVlvKx0z5nufcIOAAb9BIqlthqo/sAiOwKGbYho5TlvSXYL1PAiQJW0mM6NaD8H1Q1/82RwKBgQCzIKgiR9LfcsD8SsuVV0lZcJyVgDSFi/wO0cxAIyLfJfXqY6yjFvLOM5+yFeBpCAdwKmj29C22tKlYQl1SboiB6YjcrnZO4h8jYlFmpnSWZQborjeSrJ1T/+1eLUFc7efP0wXRm61by6VuG5iSqs/fJB7luoDv2kO5w30UTuB+jwKBgBie5GodsRoaR25PbcS6AWACr5Dtma66PeSLVtx5d0FeBSg19CttSsAo3oa2Y3grOjiaXktL+b0ZkpHj6vxm7K7iyCQL2TfkGb6Qa+0kxCGGmvMjfPYADzV8IG19d84q8HRIsz2EmwiQe5VV1G4UTJzZ5rVcH9S9NiIiG1I605C5AoGAOZu2FxhMTEqmoD0ZzlS9JUOfSEFsIssLbGSysKncLDIULaaSvfzCZ+iYYnjArGbFpaAuE8Yh8ZGWqOjnBHzB8C4AMyD0sZftdb7H+SBtfHTPTQPCoaxcCMjVOSWK0O0+UUtHosrTbSNId+nuHrVKlzQRr8ZUc08Z2uNxFZfsgScCgYEAh9PFet4/nItiJmM4ezKJdjY/wDxPkkUL3axZ6fUoOMwQW5X5ojJWR1ER6DJsvqKMAyHLsCIcyCYDpic6bFPGEC2vNGJevG3G0d/kcJOHwW/SiX2soZWx3T29ISPWmywPvZ3s0zrrRjLhebusb6vbxKofMzn5dLu5JRY7rhNm/90=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    //判断是否支付成功
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。

                        // 支付成功
                        delete_car();
                        Intent intent = new Intent(Purchase.this, MainActivity.class);
                        intent.putExtra("user", who);
                        startActivity(intent);
                        //然后将订单表中的信息复制到 待收货表 中
                        toReceive();
                        //showAlert(Purchase.this, getString(R.string.pay_success) + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        // 支付失败
                        delete_car();
                        Intent intent = new Intent(Purchase.this,MainActivity.class);
                        intent.putExtra("user", who);
                        startActivity(intent);
                        //然后将订单表中的信息复制到 待付款表 中
                        toBuy();
                        //showAlert(Purchase.this, getString(R.string.pay_failed) + payResult);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(Purchase.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(Purchase.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 支付宝支付业务示例
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void payV2() {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);

        // send total_money
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, total_money);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(Purchase.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new android.app.AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        setContentView(R.layout.purchase);

        Intent intent = getIntent();
        who = intent.getStringExtra("user");
        total_money = intent.getStringExtra("total_money");
        System.out.println("从购物车界面传过来的总价钱 === " + total_money);

        who_tv = findViewById(R.id.who);
        who_tv.setText("收件人号码：  " + who);
        order_lv = findViewById(R.id.order_lv);

        money_tv = findViewById(R.id.money);
        num_tv = findViewById(R.id.total_num);
        money_tv.setText("总计：￥" + total_money);
        showOrder();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return order_list.size();
        }

        @Override
        public Object getItem(int position) {
            return order_list.get(position);
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

            holder.img.setImageUrl(order_list.get(position).getImg());
            holder.describe_tv.setText(order_list.get(position).getDescribe());
            holder.price_tv.setText("￥" + order_list.get(position).getPrice());
            holder.number_tv.setText("x" + order_list.get(position).getNumber());

            total_number += Integer.parseInt(order_list.get(position).getNumber());

            System.out.println("展示全部订单时计算的数量 === " + total_number);
            num_tv.setText("x" + total_number);
            return convertView;
        }
    }

    //展示登录人订单 信息
    public void showOrder() {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("what_do", "order_show");
            fb.add("user", who);
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
                            MyAdapter myAdapter = new MyAdapter();
                            order_lv.setAdapter(myAdapter);

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Car>>() {
                            }.getType();
                            order_list = gson.fromJson(result, listType);

                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击支付，并删除购物车中的商品信息
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void buy_goods(View v) {
        Toast.makeText(this,total_money,Toast.LENGTH_SHORT).show();
        // 支付宝购买
        // TODO: 2019/12/30 支付宝付款实现
        payV2();

        /*AlertDialog dialog;
        dialog = new AlertDialog.Builder(this).setTitle("确认支付")
                .setMessage("确认支付？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //支付成功，首先将订单中的商品从购物车中删除
                        delete_car();
                        Intent intent = new Intent(Purchase.this, MainActivity.class);
                        intent.putExtra("user", who);
                        startActivity(intent);
                        //然后将订单表中的信息复制到 待收货表 中
                        toReceive();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击取消，但订单已经提交，将购物车中的数据删除
                        delete_car();
                        Intent intent = new Intent(Purchase.this,MainActivity.class);
                        intent.putExtra("user", who);
                        startActivity(intent);
                        //然后将订单表中的信息复制到 待付款表 中
                        toBuy();
                    }
                })
                .create();
        dialog.show();*/
    }

    //一旦选择提交订单，该商品从购物车中删除
    public void delete_car() {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("what_do", "delete");
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //将订单表中的信息复制到 待收货表 中
    public void toReceive() {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("what_do", "copy");
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
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将订单表中的信息复制到 待付款表 中
    public void toBuy() {
        //从服务器获取推荐的商品
        try {
            //1.获取OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.构造RequestBody
            FormBody.Builder fb = new FormBody.Builder();
            fb.add("what_do", "wait_buy");
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
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
