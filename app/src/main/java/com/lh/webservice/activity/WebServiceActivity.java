package com.lh.webservice.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lh.webservice.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Luhao on 2016/10/14.
 * 登陆解析
 */
public class WebServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice);

    }

    /**
     * 登陆解析
     *
     * @param username
     * @param userpassword
     * @param slnName
     * @param dcName
     * @param language
     * @param dbType
     */
    public void getRemoteInfo(final String username, final String userpassword,
                              final String slnName, final String dcName, final String language,
                              final int dbType) {

        try {
// 命名空间
            String nameSpace = "http://192.168.....";
// 调用的方法名称
            String methodName = "login";
// SOAP Action
            String soapAction = nameSpace + methodName;
            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("param1", username);
            request.addProperty("param2", userpassword);
            request.addProperty("param3", slnName);
            request.addProperty("param4", dcName);
            request.addProperty("param5", language);
            request.addProperty("param6", dbType);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER10);
            envelope.dotNet = true;
            envelope.bodyIn = request;
            envelope.setOutputSoapObject(request);
            Log.d("request", request.toString());
            envelope.encodingStyle = "UTF-8";
            HttpTransportSE transport = new HttpTransportSE(
                    "http://192.168.3.234:6892...?wsdl");// wsdl文档
        // 调用WebService
            transport.call(soapAction, envelope);
            // Object obj =envelope.getResponse();
            SoapObject result = (SoapObject) envelope.getResponse();
            Log.d("obj", result.toString());
            String result1 = null;
        // for (int i = 0; i < result.getPropertyCount(); i++) {
            result1 = result.getProperty("sessionId") + "";
        // }

            if (!result1.equals("null")) {
                // 取值
                String result2 = result.getProperty(5).toString();
                SharedPreferences sp = getSharedPreferences("logindate",
                        Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = sp.edit();
                // 存储数据
                editor.putString("sessionId", result1);
                editor.putString("userName", result2);
                // 如果登录成功为true
                editor.putBoolean("true", true);
                // 发送事物
                editor.commit();
            } else {
                SharedPreferences sp = getSharedPreferences("logindate",
                        Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = sp.edit();
                // 如果登录失败为false
                editor.putBoolean("true", false);
                // 发送事务
                editor.commit();
                // Toast.makeText(LoginActivity.this, "验证失败",Toast.LENGTH_SHORT).show();
            }

        // }

// transport.call(null, envelope);
// return result.toString();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
// return null;
        }

    }
}
