package com.example.test1.activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;


import com.example.test1.R;
import com.example.test1.databinding.ActivityLoginBinding;
import com.example.test1.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {


//    private EditText et_user,et_pwd;
    private Button login;
private ActivityLoginBinding binding;
    @Override
    protected void initView() {
        login = findViewById(R.id.login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initData() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = binding.etUser.getText().toString().trim();
                String pwd = binding.etPwd.getText().toString().trim();
                login(account,pwd);
            }
        });
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }
    public void login(String account,String pwd){
        if (StringUtils.isEmpty(account)){
            showToast("账号为空，请输入账号");
            return;
        }
        if (StringUtils.isEmpty(pwd)){
            showToast("密码为空，请输入密码");
            return;
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        Map m = new HashMap<>();
        m.put("account",account);
        m.put("pwd",pwd);
        JSONObject jsonObject = new JSONObject(m);
        String jsonStr = jsonObject.toString();
        RequestBody requestBodyJson =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonStr);
        System.out.println(jsonStr);
        //第三步创建Request
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/app/login?account="+account+"&pwd="+pwd)
                .addHeader("Content-Type","application/json;charset=UTF-8")
                .post(requestBodyJson)
                .build();

        //第四步创建call回调对象
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("登录失败：" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) throw new IOException("Unexpected response code: " + response);

                    final String result = response.body().string();
                    JSONObject jsonResponse = new JSONObject(result); // 将结果转换为JSONObject
                    int state = jsonResponse.getInt("state"); // 假设"state"是整数类型

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (state == 0){
                                JSONObject data = null;
                                try {
                                    data = jsonResponse.getJSONObject("data");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                String userId = null;
                                try {
                                    userId = String.valueOf(data.getInt("userId"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                // 存储用户 ID 到 SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", userId);
                                editor.apply();
                                navigateTo(HomeActivity.class);
                                showToast("登录成功");
                            }else {
                                showToast("登录失败");
                            }
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally {
                    response.close();
                }
            }
        });

    }



}