package com.rfid.scan.service;

import com.example.myscandemo.ScanConfig;
import com.example.myscandemo.ScanDev;
import com.rfid.scan.util.Tools;
import com.rfid.scan.util.Util;
import com.scan.service.IScan;
import com.scan.service.IScanResult;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class ScanService extends Service {
	
	private ScanDev scanDev = null;

	private boolean initFlag = false ; 
	
	private String TAG = "ScanService" ;
	
	private String result = "" ;
	private boolean scanning = false ;
	
	private boolean enterFlag = true ;
	
	private final int MSG_SCAN = 1009 ;
	
	private ScanConfig scanConfig ;
	private String prefixStr ;
	private String surfixStr ;
	
	private boolean initing = false ;
	
	//aidl feild
	private IScanResult iScanResult = null;
	private String charSet = "utf-8" ;
	
	private AIDLScan aidlScan ;
	private boolean aidlRequest = false ;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			prefixStr = scanConfig.getPrefix() ;
			surfixStr = scanConfig.getSurfix() ;
			//Log.e("scanserver", scanConfig.isVoice() + "") ;
			
			if(scanConfig.isVoice()){
				Util.play(1, 0) ;
			}
			if(msg.what == MSG_SCAN){
				if(aidlRequest){
					try {
						Log.i(TAG, result) ;
						iScanResult.onListener(result) ;
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					//input prefix
					if("\r\n".equals(prefixStr)){
						sendToInput("", true) ;
					}else{
						sendToInput(prefixStr, false) ;
					}
					//input barcode
					sendToInput(result, false) ;
					//input prefix
					if("\r\n".equals(surfixStr)){
						sendToInput("", true) ;
					}else{
						//Çå¿Õ
						if(surfixStr != null || surfixStr.length() > 0){
							byte[] surByte = surfixStr.getBytes() ;
							if("0A20202020".equals(Tools.Bytes2HexString(surByte, surByte.length))) {
								sendToInput("", true) ;
								return ;
							}
						}
						sendToInput(surfixStr, false) ;
					}

				}

			}
			
		}; 
	};
	
	
	private void sendToInput(String data , boolean enterFlag){
		Intent toBack = new Intent() ;
		toBack.setAction("android.rfid.INPUT") ;
		toBack.putExtra("data", data ) ;
		toBack.putExtra("enter", enterFlag) ;
		sendBroadcast(toBack) ;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		aidlRequest = true ;
		aidlScan = new AIDLScan() ;
		return aidlScan;
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		if(aidlScan != null)
		aidlScan = null ;
		super.unbindService(conn);
	}
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate++++") ;
		scanConfig = new ScanConfig(this) ;
		Util.initSoundPool(this) ;
		//kill server
		IntentFilter filter = new IntentFilter() ;
		filter.addAction("android.rfid.KILL_SERVER") ;
		registerReceiver(killReceiver, filter) ;
		//listner screen on/off
		IntentFilter screenFilter = new IntentFilter() ;
		screenFilter.addAction(Intent.ACTION_SCREEN_OFF) ;
		screenFilter.addAction(Intent.ACTION_SCREEN_ON) ;
		registerReceiver(powerModeReceiver, screenFilter) ;
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(killReceiver) ;
		unregisterReceiver(powerModeReceiver) ;
		super.onDestroy();
	}
	
	long startTime = 0L ;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand++++") ;
		
		//¼õÉÙÆµ·±´¥·¢
		if(startTime >0 && (System.currentTimeMillis() - startTime) < 200){
			
			return 0;
		}
		startTime = System.currentTimeMillis() ;
		if(!aidlRequest){
			if(scanDev != null){
//				mHandler.postDelayed(scanTask, 10) ;
				new Thread(scanTask).start() ;
				Log.e("scanTask", "scanTask  *****") ;
			}else{
				new Thread(initTask).start() ;
				Log.e("initTask", "initTask  +++++") ;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	//INIT THREAD
	private Runnable initTask = new Runnable() {
		
		@Override
		public void run() {
			if(initing){
				return ;
			}
			initing = true ;
			scanDev = new ScanDev() ;
			initFlag = scanDev.initDev() ;
			//init fail
			if(!initFlag){
				scanDev = null ;
			}
			initing = false ;
		}
	};
	
	//SCAN THREAD
	private Runnable scanTask = new Runnable() {
		
		@Override
		public void run() {
			if(scanning){
//				scanning = false ;
				return ;
			}
			scanning = true ;
			result = scanDev.scan() ;
			if(result != null && result.length() > 0){
				Message msg = new Message() ;
				msg.what = MSG_SCAN ;
				Bundle bundle = new Bundle() ;
				bundle.putString("data", result) ;
				msg.setData(bundle) ;
				mHandler.sendMessage(msg) ;

			}
			scanning = false ;
		}
	};
	
	
	//TO KILL SCANSERVEICE
	private BroadcastReceiver killReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getBooleanExtra("kill", false)){
				if(scanDev != null){
					scanDev.close() ;
				}
				ScanService.this.stopSelf() ;
			}
		}
	};
	
	
	//remote call 
	private class AIDLScan extends IScan.Stub{

		@Override
		public int init() throws RemoteException {
			scanDev = new ScanDev() ;
			initFlag = scanDev.initDev() ;
			if(initFlag){
				return 1 ;
			}
			return 0;
		}

		@Override
		public void close() throws RemoteException {
			if(scanDev != null){
				scanDev.close() ;
			}
			
		}

		@Override
		public void scan() throws RemoteException {
			if(scanDev != null){
				new Thread(scanTask).start() ;
			}
			
		}

		@Override
		public void setOnResultListener(IScanResult iLister)
				throws RemoteException {
			iScanResult = iLister ;
			
		}

		@Override
		public void setChar(String charSetName) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	//listner 
	private BroadcastReceiver powerModeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction() ;
			if(scanConfig.isPowerScreen()){
				//SCREEN ON ACTION
				if(action.equals(Intent.ACTION_SCREEN_ON)){
//					Log.e("powerModeReceiver", "screent on +++ ") ;
					new Thread(initTask).start() ;
				}
				//SCREEN OFF ACTION
				if(action.equals(Intent.ACTION_SCREEN_OFF)){
//					Log.e("powerModeReceiver", "screent off +++") ;
					if(scanDev != null){
						scanDev.close() ;
					}
				}
			}
			
		}
	};

}
