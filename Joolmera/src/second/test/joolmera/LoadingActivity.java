package second.test.joolmera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class LoadingActivity extends Activity {


	private Context ctx = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		//하단 버전정보 표시
		try {
			PackageInfo i = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			((TextView)findViewById(R.id.tvVersion)).setText(i.versionName);
		} catch(NameNotFoundException e) { }


		//1초 뒤에 메인 액티비티 호출.
		new Handler()
		{
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				Intent i = new Intent(ctx, MainActivity.class);
				startActivity(i);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}
}



