package com.example.myscandemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//config scan setting 
public class ScanConfig {
	
	private Context context ;
	
	public ScanConfig(Context context){
		this.context = context ;
	}

	public boolean isOpen() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("open", false);
	}

	public void setOpen(boolean open) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("open", open) ;
		editor.commit() ;
	}

	public String getPrefix() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getString("prefix", "");
	}

	public void setPrefix(String prefix) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putString("prefix", prefix) ;
		editor.commit() ;
	}

	public String getSurfix() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getString("surfix", "");
	}

	public void setSurfix(String surfix) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putString("surfix", surfix) ;
		editor.commit() ;
	}

	public boolean isVoice() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("voice", true);
	}

	public void setVoice(boolean voice) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("voice", voice) ;
		editor.commit() ;
	}

	public boolean isF1() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("f1", true);
	}

	public void setF1(boolean f1) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("f1", f1) ;
		editor.commit() ;
	}

	public boolean isF2() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("f2", true);
	}

	public void setF2(boolean f2) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("f2", f2) ;
		editor.commit() ;
	}

	public boolean isF3() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("f3", true);
	}

	public void setF3(boolean f3) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("f3", f3) ;
		editor.commit() ;
	}

	public boolean isF4() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("f4", true);
	}

	public void setF4(boolean f4) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("f4", f4) ;
		editor.commit() ;
	}

	public boolean isPowerScreen() {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		return shared.getBoolean("power", false);
	}

	public void setPowerScreen(boolean power) {
		SharedPreferences shared = context.getSharedPreferences("scanConfig", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putBoolean("power", power) ;
		editor.commit() ;
	}
	
	

}
