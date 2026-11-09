package com.sraatolye.hafizlik;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.window.OnBackInvokedDispatcher;

/**
 * Hafızlık uygulamasının Android kabuğu.
 * Uygulamanın kendisi app/src/main/assets/index.html sayfasıdır; bu sınıf onu tam ekran açar,
 * sistem çubuklarının altında kalmamasını sağlar ve geri tuşunu sayfaya iletir.
 */
public class MainActivity extends Activity {
    private static final String SAYFA = "file:///android_asset/index.html";
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean acikTema = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                != Configuration.UI_MODE_NIGHT_YES;
        int zemin = getResources().getColor(R.color.zemin, getTheme());

        FrameLayout kok = new FrameLayout(this);
        kok.setBackgroundColor(zemin);
        web = new WebView(this);
        web.setBackgroundColor(zemin);
        kok.addView(web, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(kok);

        cubuklariAyarla(kok, acikTema);
        sayfayiAyarla();
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::geri);
        }
        web.loadUrl(SAYFA);
    }

    /** Uygulama ekranı kenardan kenara çizilir; içerik durum çubuğu, gezinme çubuğu ve klavye kadar içeride durur. */
    private void cubuklariAyarla(View kok, boolean acikTema) {
        Window w = getWindow();
        w.setStatusBarColor(Color.TRANSPARENT);
        w.setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 29) {
            w.setStatusBarContrastEnforced(false);
            w.setNavigationBarContrastEnforced(false);
        }
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
            WindowInsetsController c = w.getInsetsController();
            if (c != null) {
                int acik = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                c.setSystemBarsAppearance(acikTema ? acik : 0, acik);
            }
        } else {
            int f = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            if (acikTema) f |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            w.getDecorView().setSystemUiVisibility(f);
        }
        kok.setOnApplyWindowInsetsListener((v, insets) -> {
            int sol, ust, sag, alt;
            if (Build.VERSION.SDK_INT >= 30) {
                Insets s = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                Insets k = insets.getInsets(WindowInsets.Type.ime());
                sol = s.left;
                ust = s.top;
                sag = s.right;
                alt = Math.max(s.bottom, k.bottom);
            } else {
                sol = insets.getSystemWindowInsetLeft();
                ust = insets.getSystemWindowInsetTop();
                sag = insets.getSystemWindowInsetRight();
                alt = insets.getSystemWindowInsetBottom();
            }
            v.setPadding(sol, ust, sag, alt);
            return insets;
        });
    }

    private void sayfayiAyarla() {
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // ilerleme bu depoda (localStorage) tutulur
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setUserAgentString(s.getUserAgentString() + " HafizlikApp");
        web.addJavascriptInterface(new Kopru(), "HafizlikAndroid");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest istek) {
                Uri u = istek.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, u)); // dış bağlantılar tarayıcıda açılır
                } catch (Exception e) {
                    // açacak uygulama yoksa bir şey yapma
                }
                return true;
            }
        });
    }

    /** Geri tuşu: açık pencereyi kapatır, başka sekmedeysen Bugün'e döner; yoksa uygulamayı arka plana alır. */
    private void geri() {
        if (web == null) {
            moveTaskToBack(true);
            return;
        }
        web.evaluateJavascript("(window.hafizlikBack && window.hafizlikBack()) ? 1 : 0", sonuc -> {
            if (!"1".equals(sonuc)) moveTaskToBack(true);
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        geri(); // Android 12 ve öncesi
    }

    @Override
    protected void onPause() {
        if (web != null) {
            web.evaluateJavascript("window.hafizlikFlush && window.hafizlikFlush()", null);
            web.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) {
            web.onResume();
            web.evaluateJavascript("window.hafizlikResume && window.hafizlikResume()", null);
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }

    /** Sayfanın çağırdığı küçük köprü: yedek metnini panoya kopyalar. */
    private class Kopru {
        @JavascriptInterface
        public boolean kopyala(String metin) {
            runOnUiThread(() -> {
                ClipboardManager pano = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (pano != null) pano.setPrimaryClip(ClipData.newPlainText("Hafızlık yedeği", metin));
            });
            return true;
        }
    }
}
