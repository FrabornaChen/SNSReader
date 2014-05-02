package nju.fraborna.sns21.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

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

public class OauthSinaActivity extends Activity {

	private WebView webView;
	private SharedPreferences preferences;

	private static final String TAG = "����΢����֤";
	private static final String APP_KEY = "2587784359";
	private static final String APP_SECRET = "77e76caec6a5f25acd51ca022562a8a5";

	private static final String ACCESS_TOKEN = "��������";
	private static final String EXPIRES_IN = "����������Ч��";

	private String code = "";
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
		webView.loadUrl("https://api.weibo.com/oauth2/authorize?"
				+ "client_id="
				+ APP_KEY
				+ "&response_type=code"
				+ "&scope=all&display=mobile&redirect_uri=http://open.weibo.com/&forcelogin=true");

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
					if (url0.contains("code=")) { // ��URL�н����õ�code
						code = url0.substring(url0.indexOf("code=") + 5,
								url0.length());
						try {
							code = URLDecoder.decode(code, "utf-8"); // �ƶ�Ϊutf-8����
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "code = " + code);
						if (code.length() != 0) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										String parms = "client_id="
												+ APP_KEY
												+ "&client_secret="
												+ APP_SECRET
												+ "&grant_type=authorization_code"
												+ "&code="
												+ code
												+ "&redirect_uri=http://open.weibo.com/";
										byte[] data = parms.getBytes();
										URL url2 = new URL(
												"https://api.weibo.com/oauth2/access_token");
										HttpURLConnection httpURLConnection = (HttpURLConnection) url2
												.openConnection();
										httpURLConnection
												.setConnectTimeout(3000);
										httpURLConnection
												.setRequestMethod("POST"); // ��post����ʽ�ύ
										httpURLConnection.setDoInput(true); // ��ȡ����
										httpURLConnection.setDoOutput(true); // �������д����
										// ����post�����Ҫ������ͷ
										httpURLConnection
												.setRequestProperty(
														"Content-Type",
														"application/x-www-form-urlencoded");// ����ͷ,
																								// ��������
										httpURLConnection.setRequestProperty(
												"Content-Length",
												String.valueOf(data.length));// ע�����ֽڳ���,
																				// �����ַ�����
										// ������������������������
										OutputStream outputStream = httpURLConnection
												.getOutputStream();
										// д������
										outputStream
												.write(data, 0, data.length);
										outputStream.close();
										// ��÷�������Ӧ�����״̬��
										int responseCode = httpURLConnection
												.getResponseCode();
										if (responseCode == 200) {
											// ȡ����Ӧ�Ľ��
											String result = changeInputStream(
													httpURLConnection
															.getInputStream(),
													"UTF-8");
											JSONObject jsonObject = new JSONObject(
													result);
											String access_token = jsonObject
													.getString("access_token");
											String expires_in = jsonObject
													.getString("expires_in");
											preferences = getPreferences(MODE_APPEND);
											Editor editor = preferences.edit();
											editor.clear();
											editor.putString(ACCESS_TOKEN,
													access_token);// �������õ���access_token
											// ��������
											editor.putString(EXPIRES_IN,
													expires_in);// �������õ���expires_in��������
											editor.commit();
											Log.i("sina",
													"accrss_token has got!");
										}
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).start();
							OauthSinaActivity.this.finish();
						}
					}
					super.onPageFinished(view, url);
				}
			}
		});
	}

	/**
	 * ��һ��������ת����ָ��������ַ���
	 * 
	 * @param inputStream
	 * @param encode
	 * @return
	 */
	private String changeInputStream(InputStream inputStream, String encode) {

		// �ڴ���
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
