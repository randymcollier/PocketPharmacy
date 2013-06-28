package edu.olemiss.rcollier.pocketpharmacy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Web extends Activity {
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		 
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();
		 
		Bundle data = getIntent().getExtras();
		String drug = data.getString("drug");
		String url = ("http://www.drugs.com/"+drug+".html");
		 
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
		setContentView(webview);

		webview.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 1000);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, "Sorry! " + description, Toast.LENGTH_SHORT).show();
			}
			public void onPageFinished(WebView view, String url) {
				progress.dismiss();
			}
		});

		webview.loadUrl(url);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.recreate();
    }
}
