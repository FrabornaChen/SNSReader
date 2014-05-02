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

public class OauthTencentActivity extends Activity {
	private WebView webView;
	private SharedPreferences preferences;

	private static final String TAG = "��Ѷ΢����֤";
	private static final String APP_SECRET = "c38170a77887ae70c04a1f14554d3683";
	private static final String APP_KEY = "801499919";

	private static final String ACCESS_TOKEN = "��Ѷ����";
	private static final String EXPIRES_IN = "��Ѷ������Ч��";
	private static final String OPENID = "��Ѷ�û���ʶ";

	private String access_token = "";
	private String expires_in = "";
	private String openid = "";

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
		webView.loadUrl("https://open.t.qq.com/cgi-bin/oauth2/authorize?"
				+ "client_id="
				+ APP_KEY
				+ "&response_type=token"
				+ "&redirect_uri=http://wiki.open.t.qq.com/index.php/&forcelogin=true");

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
								url0.indexOf("openid") - 1);
						Log.i(TAG, "expires_in = " + expires_in);
					}
					if (url0.contains("openid=")) { // ��URL�н����õ� openid
						openid = url0.substring(url0.indexOf("openid=") + 7,
								url0.indexOf("openkey=") - 1);
						Log.i(TAG, "openid = " + openid);
					}

					preferences = getPreferences(MODE_APPEND);
					Editor editor = preferences.edit();
					editor.clear();
					editor.putString(ACCESS_TOKEN, access_token);// �������õ���access_token
																	// ��������
					editor.putString(EXPIRES_IN, expires_in);// �������õ���expires_in��������
					editor.putString(OPENID, openid);
					editor.commit();

					// �����û����������½�ɹ�������ҳ����ת
					if (access_token.length() != 0) {
						Log.i(TAG, "Binding Renren Successed!");
						Toast.makeText(OauthTencentActivity.this,
								"����Ѷ΢���˺ųɹ�!", Toast.LENGTH_SHORT).show();
						OauthTencentActivity.this.finish();
					}
				}
				super.onPageFinished(view, url);
			}
		});
	}
}
