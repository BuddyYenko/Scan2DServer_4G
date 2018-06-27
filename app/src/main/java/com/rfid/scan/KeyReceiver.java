package com.rfid.scan;

import com.example.myscandemo.ScanConfig;
import com.rfid.scan.service.ScanService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class KeyReceiver extends BroadcastReceiver {

	private String TAG = "KeyReceiver" ;
	@Override
	public void onReceive(Context context, Intent intent) {
		int keyCode = intent.getIntExtra("keyCode", 0) ;
		if(keyCode == 0){//ºÊ»›H941
			keyCode = intent.getIntExtra("keycode", 0) ;
		}
		boolean keyDown = intent.getBooleanExtra("keydown", false) ;
		Log.e(TAG, "KEYcODE = " + keyCode + ", Down = " + keyDown);
		ScanConfig config = new ScanConfig(context) ;
		if(keyDown && config.isOpen()){
			Intent toService = new Intent(context, ScanService.class) ;
			switch (keyCode) {
			case KeyEvent.KEYCODE_F1:
				if(config.isF1()){
					context.startService(toService) ;	
				}
				break;
			case KeyEvent.KEYCODE_F2:
				if(config.isF2()){
					context.startService(toService) ;	
				}
				break;
			case KeyEvent.KEYCODE_F3:
				if(config.isF3()){
					context.startService(toService) ;	
				}
				break;
			case KeyEvent.KEYCODE_F4:
				if(config.isF4()){
					context.startService(toService) ;	
				}
				break;
		case KeyEvent.KEYCODE_F5:
			if(config.isF4()){
				context.startService(toService) ;	
			}
			break;
		}
			
			
		}

//		Intent toActivity = new Intent(context, ConfigActivity.class) ;
//		toActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
//		context.startActivity(toActivity) ;
	}

}
