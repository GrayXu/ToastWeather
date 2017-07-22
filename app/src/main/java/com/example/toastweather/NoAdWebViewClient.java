package com.example.toastweather;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Xgl on 2017/7/22.
 * 通过屏蔽广告所在div的方法来关闭广告
 */

public class NoAdWebViewClient extends WebViewClient {

    private Context context;
    private WebView webView;
    private boolean isClose;

    public NoAdWebViewClient(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            String js = ADFilterTool.getClearAdDivJs(context);
//            Log.v("adJs",js);
//            webView.loadUrl(js);
//        }
//    };
//
//    @Override
//    public void onPageFinished(WebView view, String url) {
//        super.onPageFinished(view, url);
//        isClose = false;
//    }
//
//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        super.onPageStarted(view, url, favicon);
//        if(isClose){
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isClose = true;
//                while (isClose){
//                    try {
//                        Thread.sleep(2500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessage(0x001);
//                }
//            }
//        }).start();
//    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String js = ADFilterTool.getClearAdDivJs(context);
        Log.v("adJs",js);
        view.loadUrl(js);
    }

    /**
     * 内部静态类作为筛选工具
     */
    public static class ADFilterTool{

        public static String getClearAdDivJs(Context context){
            StringBuilder js = new StringBuilder("javascript:");
//            String js = "javascript:";
            Resources res = context.getResources();

            //屏蔽掉可获得id的div
            String[] adDivs = res.getStringArray(R.array.adBlockDivIds);
            int i;
            for(i=0;i<adDivs.length;i++){
                //注入的JS的代码
                js.append("var adDiv"+i+"= document.getElementById('"+adDivs[i]+"');if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            }
            //通过Tag名称来获取无id的组件
            i++;
            js.append("var adDiv"+i+"= document.getElementsByTagName('h1')[0];if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            i++;
            js.append("var adDiv"+i+"= document.getElementsByTagName('a')[0];if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            i++;
            js.append("var adDiv"+i+"= document.getElementsByTagName('img')[0];if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            i++;
            js.append("var adDiv"+i+"= document.getElementsByTagName('div')[4];if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            i++;
            js.append("var adDiv"+i+"= document.getElementsByClassName('footer')[0];if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");");
            return js.toString();
        }

    }
}
