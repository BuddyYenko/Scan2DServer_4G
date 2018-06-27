package com.rfid.scan;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myscandemo.R;
import com.example.myscandemo.ScanConfig;
import com.rfid.scan.service.ScanService;

public class ConfigActivity extends Activity {

	private ScanConfig scanConfig;

	private Switch openSwitch;
	private Spinner spinnerPrefix;
	private Spinner spinnerSurfix;
	private CheckBox checkVoice;
	private CheckBox checkF1;
	private CheckBox checkF2;
	private CheckBox checkF3;
	private CheckBox checkPowerScreen ;
	private CheckBox checkF4;
	private TextView tvPrefix ;
	private TextView tvSurfix ;
	
	private String tabStr;
	private   String spaceStr;
	private   String enterStr;
	private   String noneStr;
	private   String otherStr;
	private String[] fixArray;
	
	private Dialog dialogLoading ;
	private Dialog dialogOther ;
	
	private final int MSG_CANSEL_DIALOG = 1003 ;
	private Handler mHandler  = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CANSEL_DIALOG:
				dialogLoading.cancel() ;
				scanConfig.setOpen(true) ;
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		scanConfig = new ScanConfig(this);
		
		//listner screen on/off
		IntentFilter screenFilter = new IntentFilter() ;
		screenFilter.addAction(Intent.ACTION_SCREEN_OFF) ;
		screenFilter.addAction(Intent.ACTION_SCREEN_ON) ;
		registerReceiver(powerModeReceiver, screenFilter) ;
		initView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(powerModeReceiver) ;
		super.onDestroy();
	}
	
	private void initView() {
		tabStr = getResources().getString(R.string.tab);
		spaceStr = getResources().getString(R.string.space);
		enterStr = getResources().getString(R.string.enter);
		noneStr = getResources().getString(R.string.none);
		otherStr = getResources().getString(R.string.other);
		fixArray = new String[] { tabStr, spaceStr, enterStr, noneStr, otherStr };
		tvPrefix = (TextView) findViewById(R.id.text_prefix) ;
		tvSurfix = (TextView) findViewById(R.id.text_surfix) ;

		openSwitch = (Switch) findViewById(R.id.switch_scan);
		spinnerPrefix = (Spinner) findViewById(R.id.spinner_prefix);
		spinnerSurfix = (Spinner) findViewById(R.id.spinner_surfix);
		checkVoice = (CheckBox) findViewById(R.id.checkBox_voice);
		checkF1 = (CheckBox) findViewById(R.id.checkBox_f1);
		checkF2 = (CheckBox) findViewById(R.id.checkBox_f2);
		checkF3 = (CheckBox) findViewById(R.id.checkBox_f3);
		checkF4 = (CheckBox) findViewById(R.id.checkBox_f4);
		checkPowerScreen = (CheckBox) findViewById(R.id.checkBox_power_saving) ;

		openSwitch.setChecked(scanConfig.isOpen()) ;
		checkF1.setChecked(scanConfig.isF1());
		checkF2.setChecked(scanConfig.isF2());
		checkF3.setChecked(scanConfig.isF3());
		checkF4.setChecked(scanConfig.isF4());
		checkVoice.setChecked(scanConfig.isVoice());
		checkPowerScreen.setChecked(scanConfig.isPowerScreen()) ;

		spinnerPrefix.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, fixArray));
		spinnerPrefix.setSelection(3);
		spinnerSurfix.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, fixArray));
		spinnerSurfix.setSelection(2);
		// set key
		checkF1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scanConfig.setF1(isChecked);
			}
		});

		checkF2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scanConfig.setF2(isChecked);
			}
		});
		checkF3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scanConfig.setF3(isChecked);
			}
		});
		checkF4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scanConfig.setF4(isChecked);
			}
		});
		checkVoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scanConfig.setVoice(isChecked);
			}
		});
		
		checkPowerScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				scanConfig.setPowerScreen(isChecked) ;
				
			}
		}) ;

		// open dev
		openSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// open scan
				if (isChecked) {
					createLoaddingDialog() ;
					Intent toService = new Intent(ConfigActivity.this, ScanService.class) ;
					startService(toService) ;
					Timer timer = new Timer() ;
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							Message msg = new Message() ;
							msg.what = MSG_CANSEL_DIALOG ;
							mHandler.sendMessage(msg) ;
							
						}
					}, 3500) ;
					
				} else {
					Intent toKill = new Intent() ;
					toKill.setAction("android.rfid.KILL_SERVER") ;
					toKill.putExtra("kill", true) ;
					sendBroadcast(toKill) ;
//					checkPowerScreen.setChecked(false) ;
//					scanConfig.setPowerScreen(false) ;
					scanConfig.setOpen(false) ;
				}

			}
		});

		// prefix
		spinnerPrefix.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
//				try {
//			        Field field =       AdapterView.class.getDeclaredField("mOldSelectedPosition");
//			                field.setAccessible(true);  //设置mOldSelectedPosition可访问
//			                field.setInt(spinnerPrefix, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
//			    } catch (Exception e) {
//			                e.printStackTrace();
//			    }
				if(fixArray[position].equals(tabStr)){
					scanConfig.setPrefix("\t");
				}else if(fixArray[position].equals(spaceStr)){
					scanConfig.setPrefix(" ");
				}else if(fixArray[position].equals(enterStr)){
					scanConfig.setPrefix("\r\n");
				}else if(fixArray[position].equals(noneStr)){
					scanConfig.setPrefix("");
				}else if(fixArray[position].equals(otherStr)){
					//dialog input prefix char
					createOtherDialog(true) ;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.e("nothing selected", "") ;

			}
		});

		// surfix
		spinnerSurfix.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(fixArray[position].equals(tabStr)){
					scanConfig.setSurfix("\t");
				}else if(fixArray[position].equals(spaceStr)){
					scanConfig.setSurfix(" ");
				}else if(fixArray[position].equals(enterStr)){
					scanConfig.setSurfix("\r\n");
				}else if(fixArray[position].equals(noneStr)){
					scanConfig.setSurfix("");
				}else if(fixArray[position].equals(otherStr)){
					//dialog input surfix char
					createOtherDialog(false) ;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}
	
	//create loading dialog
	private void createLoaddingDialog(){
		Builder  builder = new Builder(this) ;
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null) ;
		builder.setView(view) ;
		dialogLoading = builder.create() ;
		dialogLoading.setCancelable(false) ;
		dialogLoading.show() ;
	}
	
	
	private EditText editUserChar ;
	private void createOtherDialog(final boolean isPrefix){
		//Input the customized Char
		Builder  builder = new Builder(this) ;
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_others, null) ;
		editUserChar = (EditText) view.findViewById(R.id.editText_others) ;
		builder.setView(view) ;
		builder.setTitle(getResources().getString(R.string.user_char)) ;
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogOther.cancel() ;
			}
		}) ;
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String userChar = editUserChar.getText().toString() ;
				if(isPrefix){
					tvPrefix.setText(userChar) ;
					scanConfig.setPrefix(userChar) ;
				}else{
					tvSurfix.setText(userChar) ;;
					scanConfig.setSurfix(userChar) ;
				}
				
				dialogOther.cancel() ;
			}
		}) ;
		
		dialogOther = builder.create() ;
//		dialogOther.setCancelable(false) ;
		dialogOther.show() ;
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
					openSwitch.setChecked(true) ;
				}
				//SCREEN OFF ACTION
				if(action.equals(Intent.ACTION_SCREEN_OFF)){
//					Log.e("powerModeReceiver", "screent off +++") ;
					openSwitch.setChecked(false) ;
				}
			}
			
		}
	};
}
