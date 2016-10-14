package com.lh.webservice.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lh.webservice.R;
import com.lh.webservice.utils.webserviceutils.WebServiceUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Luhao on 2016/10/14.
 * 登陆解析
 * <p/>
 * 1.创建HttpTransportSE传输对象：HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
 * SERVICE_URL是webservice提供服务的url
 * <p/>
 * 2.使用SOAP1.1协议创建Envelop对象：SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
 * 设置SOAP协议的版本号，根据服务端WebService的版本号设置。
 * <p/>
 * 3.实例化SoapObject对象：SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, methodName);
 * 第一个参数表示WebService的命名空间，可以从WSDL文档中找到WebService的命名空间。第二个参数表示要调用的WebService方法名。
 * <p/>
 * 4.设置调用方法的参数值，如果没有参数，可以省略：
 * 例如soapObject.addProperty("theCityCode", cityName);
 * <p/>
 * 5.记得设置bodyout属性 envelope.bodyOut = soapObject;
 * <p/>
 * 6.调用webservice：ht.call(SERVICE_NAMESPACE+methodName, envelope);
 * <p/>
 * 7.获取服务器响应返回的SOAP消息：
 * SoapObject result = (SoapObject) envelope.bodyIn;
 * SoapObject detail = (SoapObject) result.getProperty(methodName+"Result");
 */
public class WebServiceActivity extends AppCompatActivity {

    private EditText webservice_edit;
    private TextView webservice_text;
    private Button webservice_btn;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                webservice_text.setText(msg.obj.toString());
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice);
        webservice_edit = (EditText) findViewById(R.id.webservice_edit);
        webservice_btn = (Button) findViewById(R.id.webservice_btn);
        webservice_text = (TextView) findViewById(R.id.webservice_text);
        webservice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.what=1;
                        message.obj= WebServiceUtil.getRemoteInfo(webservice_edit.getText().toString().trim());
                        handler.sendMessage(message);
                    }
                }).start();

            }
        });
    }

    /**
     * 登陆解析
     *
     * @param username     用户名
     * @param userpassword 密码
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
