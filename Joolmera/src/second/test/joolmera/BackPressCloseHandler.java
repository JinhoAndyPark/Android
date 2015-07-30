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
	                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
	        toast.show();
	    }
}
