package com.example.myapplication.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class AiChatService {

    private static final String TAG = "AiChatService";

    // ===== 硅基流动配置 =====
    private static final String API_URL =
            "https://api.siliconflow.cn/v1/chat/completions";

    // ⚠️ 把你的 API Key 填这里
    private static final String API_KEY = "sk-sophmdllipyoqezbsybmfpmmetuhsvutuwtxqwphriexdtig";

    // 🔥 换成响应快的非推理模型（关键！）
    private static final String MODEL_NAME = "Qwen/Qwen2.5-7B-Instruct";

    // 超时时间（毫秒）
    private static final int CONNECT_TIMEOUT = 30000;   // 30秒
    private static final int READ_TIMEOUT = 90000;      // 90秒（推理模型需要）

    // 重试次数
    private static final int MAX_RETRY = 2;

    public interface Callback {
        void onSuccess(String reply);
        void onError(String error);
    }

    /** AI 扮演好友回复 */
    public static void getFriendReply(String friendName, String signature,
                                      String gender, String userMessage,
                                      Callback callback) {
        StringBuilder sysPrompt = new StringBuilder();
        sysPrompt.append("你扮演一个名叫「").append(friendName).append("」的");
        if (gender != null && !gender.isEmpty()) sysPrompt.append(gender).append("性");
        sysPrompt.append("朋友，在社交软件上和对方聊天。");
        if (signature != null && !signature.isEmpty()) {
            sysPrompt.append("个性签名：").append(signature).append("。");
        }
        sysPrompt.append("用口语化、简短的中文回复（不超过20字），")
                .append("像真人朋友聊天，不要自我介绍，不要说你是AI。");

        getReply(sysPrompt.toString(), userMessage, callback);
    }

    public static void getReply(String message, Callback callback) {
        getReply("你是一个友好的AI助手，简短回答问题。", message, callback);
    }

    /** 核心请求（带自动重试） */
    public static void getReply(String systemPrompt, String userMessage, Callback callback) {
        new Thread(() -> {
            if (API_KEY == null || API_KEY.isEmpty() || API_KEY.contains("你的硅基流动")) {
                callback.onError("API Key 未配置");
                return;
            }

            String lastError = null;

            for (int attempt = 0; attempt <= MAX_RETRY; attempt++) {
                if (attempt > 0) {
                    Log.d(TAG, "第 " + (attempt + 1) + " 次尝试");
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                }

                HttpURLConnection conn = null;
                try {
                    URL url = new URL(API_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                    conn.setRequestProperty("Connection", "close");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(CONNECT_TIMEOUT);
                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setUseCaches(false);

                    // 构建请求
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("model", MODEL_NAME);
                    jsonBody.put("temperature", 0.7);
                    jsonBody.put("max_tokens", 100);   // 限制长度，加快响应

                    JSONArray messages = new JSONArray();
                    if (systemPrompt != null && !systemPrompt.isEmpty()) {
                        JSONObject sys = new JSONObject();
                        sys.put("role", "system");
                        sys.put("content", systemPrompt);
                        messages.put(sys);
                    }
                    JSONObject user = new JSONObject();
                    user.put("role", "user");
                    user.put("content", userMessage);
                    messages.put(user);
                    jsonBody.put("messages", messages);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonBody.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    Log.d(TAG, "响应码：" + code);

                    if (code == 200) {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) sb.append(line);
                        in.close();

                        JSONObject resp = new JSONObject(sb.toString());
                        String reply = resp.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        Log.i(TAG, "AI 回复成功：" + reply);
                        callback.onSuccess(reply.trim());
                        return;   // 成功就结束
                    } else {
                        // 4xx 错误，重试没用
                        BufferedReader errIn = new BufferedReader(
                                new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        StringBuilder eb = new StringBuilder();
                        String el;
                        while ((el = errIn.readLine()) != null) eb.append(el);
                        errIn.close();

                        String em = "HTTP " + code + " : " + eb.toString();
                        Log.e(TAG, em);

                        if (code >= 400 && code < 500) {
                            callback.onError(em);   // 客户端错误，不重试
                            return;
                        }
                        lastError = em;
                    }

                } catch (java.net.SocketTimeoutException e) {
                    lastError = "超时（第 " + (attempt + 1) + " 次）";
                    Log.w(TAG, "超时，准备重试", e);
                } catch (Exception e) {
                    lastError = e.getClass().getSimpleName() + "：" + e.getMessage();
                    Log.e(TAG, "异常", e);
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }

            // 所有重试都失败
            callback.onError("多次请求失败：" + lastError);
        }).start();
    }
}