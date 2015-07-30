package second.test.joolmera;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
	
	
	 private long backKeyPressedTime = 0;
	    private Toast toast;
	 
	    private Activity activity;
	 
	    public BackPressCloseHandler(Activity context) {
	        this.activity = context;
	    }
	 
	    public void onBackPressed() {
	        if (System.currentTimeMillis() > backKeyPressedTime + 2000 
	        		&& DataConstants.flag_isPanoramaselect == false 
	        		&& DataConstants.flag_isImageTransfer == false) {
	            backKeyPressedTime = System.currentTimeMillis();
	            showGuide();
	            return;
	        }
	        if (System.currentTimeMillis() <= backKeyPressedTime + 2000 
	        		&& DataConstants.flag_isPanoramaselect == false
	        		&& DataConstants.flag_isImageTransfer == false) {
	        	activity.finish();
	            toast.cancel();
	        }
	    }
	 
	    public void showGuide() {
	        toast = Toast.makeText(activity,
	                "\'�ڷ�\'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT);
	        toast.show();
	    }
}
