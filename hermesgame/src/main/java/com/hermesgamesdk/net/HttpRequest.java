package com.hermesgamesdk.net;

import com.quickjoy.lib.jkhttp.Parameter;
import com.quickjoy.lib.jkhttp.Request;

import java.lang.reflect.ParameterizedType;



/**
 * @param <T> 网络请求成功后，根据请求传入的泛型 返回对象，对象中已经根据返回json结果赋值
 * HttpRequest 用于构建网络请求Request对象
 */
public abstract class HttpRequest<T> {
    private Parameter.Builder parameterBuild;
    private Request.Builder requestBuild;
    private Class<T> clz;
    private String url;
    
    /**
     * @return 获取传入的泛型类型
     */
    @SuppressWarnings("unchecked")
    public Class<T> getClz() {
        if (clz == null) {
			clz = (Class<T>) (((ParameterizedType) this.getClass()
                    .getGenericSuperclass()).
                    getActualTypeArguments()[0]);

        }
        return clz;
    }

    /**
     * @param bean 封装了数据的泛型对象
     * 网络请求成功后回调
     */
    public abstract void onSuccess(T bean);

    /**
     * @param message 网络请求失败信息
     * 网络失败后回调方法
     */
    public abstract void onFailed(int code,String message);
    
 /*   public  void onFailed(int code,String message) {
    	onFailed(message);
    }*/

    public HttpRequest() {
        parameterBuild = new Parameter.Builder();
        requestBuild = new Request.Builder();
    }


    
    /**
     * @param parameters {@link QGParameter} 构建的服务hermesgame参数格式的字符串
     * data=value&data=value,解析此格式字符串并传入网络请求参数
     * @return
     * 
     */
    public HttpRequest<T> addParameter(String parameters) {
    	//TODO 越界判断等
        String[] params = parameters.split("&");
        for (String param : params) {
            String[] str = param.split("=");
            String key = str[0];
            String value = str[1];
            parameterBuild.addParameter(key, value);
        }

        return this;
    }
    /**
     * 网络请求URL
     */
    public HttpRequest<T> setUrl(String url) {
        requestBuild.url(url);
        this.url = url;
        return this;
    }
    public String getUrl() {
       return url;
    }
    /**
     * 网络请求Method:GET
     */
    public HttpRequest<T> get() {
        requestBuild.get();
        return this;
    }

    /**
     * 网络请求Method:POST
     */
    public HttpRequest<T> post() {
        requestBuild.post();
        return this;
    }

    /**   
     * 获取构建 Request
     */
    public Request getRequest() {

        Request request = requestBuild.parameter(parameterBuild.build()).build();
        return request;
    }

    /**   
     * 添加网络请求头部信息
     */
    public HttpRequest<T> header(String key, String value) {
        requestBuild.header(key, value);

        return this;
    }
    public HttpRequest<T> addHeader(String key, String value) {
        requestBuild.addHeader(key, value);

        return this;
    }
}
