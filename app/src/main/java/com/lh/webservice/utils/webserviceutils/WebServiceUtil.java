package com.lh.webservice.utils.webserviceutils;

import android.text.TextUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Luhao on 2016/10/14.
 * webservice工具类
 */
public class WebServiceUtil {

    // 定义webservice的命名空间；
    public static final String targetNamespace = "http://WebXml.com.cn/";

    // 定义webservice提供服务的url ；是将WSDL地址末尾的"?wsdl"去除后剩余的部分
    //查询天气
    public static final String endPoint = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx";
    //查询手机号归属地
    //public static final String endPoint = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";

    //调用的方法名；
    //查询天气
    public static final String elementFormDefault_Name = "getRegionDataset";
    //查询手机号归属地
    //public static final String elementFormDefault_Name="getMobileCodeInfo";

    //soap 行动 ；命名空间 + 调用的方法名称
    public static final String soapAction = targetNamespace + elementFormDefault_Name;

    //调用参数
    //查询天气的参数
    public static final String mobileCode = "mobileCode";
    public static final String userId = "userId";

    //返回值的参照值
    //查询手机归属地
    //public static final String backObj = "getMobileCodeInfoResult";
    //查询天气
    public static final String backObj = "getRegionProvinceResult";

    /**
     * 手机号段归属地查询
     *
     * @param phoneSec 手机号段
     */
    public static String getRemoteInfo(String phoneSec) {

        if (TextUtils.isEmpty(phoneSec)) return "电话号码输入不正确";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(targetNamespace, elementFormDefault_Name);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty(mobileCode, phoneSec);
        rpc.addProperty(userId, "");

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        envelope.bodyOut = rpc;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
            // 获取返回的数据
            return envelope.getResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 第二种获取返回的数据的方式
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        return object.getProperty(backObj).toString();
    }


}
