package com.example.credencialdigitalcesba;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ActivityResultLauncher<Void> camera;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(7,17,39));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(7,17,39));
        getWindow().getDecorView().setSystemUiVisibility(0);
        camera=registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if(bitmap!=null) {
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,88,out);
                String encoded=Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);
                webView.evaluateJavascript("window.setPhoto('data:image/jpeg;base64,"+encoded+"')",null);
            }
        });
        WebViewAssetLoader assetLoader=new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this))
                .build();
        webView=new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.rgb(7,17,39));
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onConsoleMessage(android.webkit.ConsoleMessage message){
                android.util.Log.e("CESBA-WebView",message.message()+" (línea "+message.lineNumber()+")");
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
            @Override public WebResourceResponse shouldInterceptRequest(WebView view,String url){
                return assetLoader.shouldInterceptRequest(android.net.Uri.parse(url));
            }
            @Override public void onReceivedError(WebView view,android.webkit.WebResourceRequest request,android.webkit.WebResourceError error){
                super.onReceivedError(view,request,error);
                android.util.Log.e("CESBA-WebView","No se pudo cargar "+request.getUrl()+": "+error.getDescription());
            }
        });
        webView.addJavascriptInterface(new Bridge(),"CESBA");
        setContentView(webView);
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }
    private class Bridge {
        @JavascriptInterface public void openCamera(){runOnUiThread(()->camera.launch(null));}
        @JavascriptInterface public void makeQr(String contents){
            try {
                BitMatrix matrix=new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE,420,420);
                Bitmap bitmap=Bitmap.createBitmap(420,420,Bitmap.Config.ARGB_8888);
                for(int y=0;y<420;y++)for(int x=0;x<420;x++)bitmap.setPixel(x,y,matrix.get(x,y)?android.graphics.Color.BLACK:android.graphics.Color.WHITE);
                ByteArrayOutputStream out=new ByteArrayOutputStream();bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                String data=Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);
                runOnUiThread(()->webView.evaluateJavascript("window.setQr('"+data+"')",null));
            } catch(Exception ignored) { }
        }
    }
    @Override public void onBackPressed(){webView.evaluateJavascript("window.nativeBack ? window.nativeBack() : 'exit'", value->{if(value==null||value.contains("exit"))super.onBackPressed();});}
}
