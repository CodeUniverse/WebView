/*
 * Copyright (C) 2013 Code Universe. All rights reserved.
 * 
 * http://www.codeuniverse.org
 * 
 * Created by Jaison Brooks 4/26/2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeuniverse.webview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	String weburl = "http://www.codeuniverse.org";
	WebView web;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Here is all of our WebView Components
		web = (WebView) findViewById(R.id.webView1);
		web.setBackgroundColor(Color.parseColor("#ffffff"));
		web.setWebViewClient(new myWebClient());
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		web.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(progress);
			}
		});
		WebSettings websettings = web.getSettings();
		websettings.setBuiltInZoomControls(true);
		websettings.setUseWideViewPort(true);
		websettings.setJavaScriptEnabled(true);
		websettings.setAllowFileAccess(true);
		websettings.setDomStorageEnabled(true);
		websettings.setLoadWithOverviewMode(true);
		websettings.setUserAgentString("Android");
		websettings.setAppCacheEnabled(true);
		websettings.setSavePassword(true);

		// Load the URL you want to load
		web.loadUrl(weburl);
		// Also can be used like this
		// web.loadUrl("http://www.url.com"");
	}

	public class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(final WebView view, final String url,
				Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			// Show the progress bar when page starts loading
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			// Here we allow other links to be clicked and opened
			view.loadUrl(url);
			// Make sure the progress bar is visable
			progressBar.setVisibility(View.VISIBLE);
			return true;
		}

		public void onProgressChanged(WebView view, int progress) {
			// Here is where the page is loading
			progressBar.setProgress(progress);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// Here you can place code to execute when there is a error, like No
			// data or a bad URL
		};

		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			// Here we disable the progress bar when the page finishs loading
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(android.view.MenuItem Item) {
		switch (Item.getItemId()) {

		case R.id.menu_refresh:
			web.reload();
			return true;
		case R.id.menu_back:
			web.goBack();
			return true;
		case R.id.menu_forward:
			web.goForward();
			return true;
		case R.id.menu_capture:
			// Resize the webview to the height of the webpage
			int pageHeight = web.getContentHeight();
			LayoutParams browserParams = web.getLayoutParams();
			web.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, pageHeight));

			// Capture the webview as a bitmap
			web.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(web.getDrawingCache());
			web.setDrawingCacheEnabled(false);

			String out = new SimpleDateFormat("EEE_MMM_dd_yyyy hh:mm.s'.jpg'")
					.format(new Date());

			// Create the filename to use
			String target_filename = "CodeUniverse-" + (out);
			// + ".jpg";
			try {
				File targetDir = new File(
						Environment.getExternalStorageDirectory(),
						"/CodeUniverse");
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
				File file = new File(targetDir, target_filename);
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				Toast.makeText(
						MainActivity.this,
						"Capture Saved!\n\nImage Stored @ /sdcard/CodeUniverse",
						Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(MainActivity.this,
						"Problem Storing Image, Check your SD Card",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				web.setLayoutParams(browserParams);
			}
			return true;
		case R.id.menu_text_increase:
			WebSettings websettings = web.getSettings();
			websettings.setTextZoom((int) (websettings.getTextZoom() * 1.1));
			return true;
		case R.id.menu_text_decrease:
			WebSettings websettings2 = web.getSettings();
			websettings2.setTextZoom((int) (websettings2.getTextZoom() / 1.1));
			return true;
		case R.id.menu_full_browser:
			Intent myIntent = new Intent(getBaseContext(), FullBrowser.class);
			startActivity(myIntent);
			return true;

		}
		return super.onOptionsItemSelected(Item);
	}

	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if ((KeyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		web.removeAllViews();
		web.destroy();
	}

}
