package com.example.test1.fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test1.R;
import com.example.test1.adpter.AiAdapter;
import com.example.test1.entity.Msg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AiFragment extends BaseFragment {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private AiAdapter adapter;
    private String message;
    private String returnMessage;
    private List<Msg> msgList = new ArrayList<Msg>();
    public AiFragment() {
    }
    public static Fragment newInstance() {
        AiFragment fragment = new AiFragment();
        return fragment;
    }
    @Override
    protected int initLayout() {
        return R.layout.fragment_ai;
    }

    @Override
    protected void initView() {
        adapter = new AiAdapter(getContext(), R.layout.msg_item, msgList);
        inputText = mRootView.findViewById(R.id.input_text);
        send = mRootView.findViewById(R.id.send);
        msgListView = mRootView.findViewById(R.id.msg_list_view);
    }
    @Override
    protected void initData() {
        initMsgs(); // 初始化消息数据
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);// 当有新消息时，刷新ListView中的显示
                    msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容
                    message = content;
                    getMessage();
                }
            }
        });
    }

    public void getMessage() {
        OkHttpClient client = new OkHttpClient();
        // 构建请求
        Request request = new Request.Builder()
                .url("http://110.41.60.211:8080/ai?message=" + message) // 设置URL
                .get() // 设置为GET请求
                .build();
        // 发送请求
        Call call = client.newCall(request);
        call.enqueue(new Callback() { // 异步执行请求
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(AiFragment.class.getSimpleName(), "请求失败: " + e.getMessage());
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 请求成功时调用
                if (!response.isSuccessful()) {
                    // 如果响应不成功则记录错误日志
                    Log.e("NewsFragment", "响应失败: " + response.code());
                    return;
                }
                String result = response.body().string();

                    try {
                        JSONObject  jsonObject = new JSONObject(result);
                        returnMessage = jsonObject.getString("data");
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                adapter.handler.removeCallbacks(adapter.typingRunnable);
                                Msg msg = new Msg(returnMessage, Msg.TYPE_RECEIVED);
                                msgList.add(msg);
                                msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } finally {
                        // 关闭响应体
                        response.body().close();
                    }
            }
        });
    }
    private void initMsgs() {
        Msg msg1 = new Msg("您好，我是阿里云开发的一款超大规模语言模型，我叫通义千问。我能够回答问题、创作文字，还能表达观点、撰写代码。如果您有任何问题或需要帮助，请随时告诉我，我会尽力提供支持。", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
    }
}