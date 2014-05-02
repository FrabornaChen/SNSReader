package nju.fraborna.sns21.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import nju.fraborna.sns21.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class OauthRenrenActivity extends Activity {

	private WebView webView;
	private SharedPreferences preferences;

	private static final String TAG = "��������֤";
	private static final String APP_ID = "267123";
	private static final String APP_KEY = "99bad0611e0242c6bb2ce014e5fe6a5c";
	private static final String SECRET_KEY = "5b696fb160e942548568d1c735793f61";

	private static final String ACCESS_TOKEN = "��������";
	private static final String EXPIRES_IN = "����������Ч��";

	private String access_token = "";
	private String expires_in = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_sina);

		webView = (WebView) findViewById(R.id.webview);

		// ��WebView�������ã���JS��֧�֣������ŵ�֧�֣��Ի���ģʽ��֧�֣�
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		// ����client_idȡ�õ����˷��������˶����Ӧ����Ȩ������ɹ��򷵻���������½ҳ���html�ļ�������WebView�ؼ�����ʾ
		// ��ʱ�û���Ҫ�����Լ������˺ŵ��û��������벢�����½
		webView.loadUrl("https://graph.renren.com/oauth/authorize?"
				+ "client_id="
				+ APP_KEY
				+ "&response_type=token"
				+ "&scope=read_user_feed,publish_feed&display=touch&redirect_uri=http://graph.renren.com/oauth/login_success.html&x_renew=true");

		webView.setWebViewClient(new WebViewClient() {

			// �����ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();// ��webview����https����
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				String url0 = webView.getUrl();
				Log.i(TAG, "URL = " + url0);
				if (url0 != null) {
					if (url0.contains("access_token=")) { // ��URL�н����õ�
															// access_token
						access_token = url0.substring(
								url0.indexOf("access_token=") + 13,
								url0.indexOf("expires_in") - 1);
						try {
							access_token = URLDecoder.decode(access_token,
									"utf-8"); // �ƶ�Ϊutf-8����
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "access_token = " + access_token);
					}
					if (url0.contains("expires_in=")) { // ��URL�н����õ� expires_in
						expires_in = url0.substring(
								url0.indexOf("expires_in=") + 11,
								url0.indexOf("scope") - 1);
						Log.i(TAG, "expires_in = " + expires_in);
					}

					preferences = getPreferences(MODE_APPEND);
					Editor editor = preferences.edit();
					editor.clear();
					editor.putString(ACCESS_TOKEN, access_token);// �������õ���access_token
																	// ��������
					editor.putString(EXPIRES_IN, expires_in);// �������õ���expires_in��������
					editor.commit();

					// �����û����������½�ɹ�������ҳ����ת
					if (access_token.length() != 0) {
						Log.i(TAG, "Binding Renren Successed!");
						Toast.makeText(OauthRenrenActivity.this, "�������˺ųɹ�!",
								Toast.LENGTH_SHORT).show();
						OauthRenrenActivity.this.finish();
					}
				}
				super.onPageFinished(view, url);
			}
		});
	}
}
