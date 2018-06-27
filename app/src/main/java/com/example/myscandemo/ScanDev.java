package com.example.myscandemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hsm.barcode.DecodeOptions;
import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.ImageAttributes;
import com.hsm.barcode.DecoderConfigValues.LightsMode;
import com.hsm.barcode.DecoderConfigValues.OCRMode;
import com.hsm.barcode.DecoderConfigValues.OCRTemplate;
import com.hsm.barcode.DecoderConfigValues.SymbologyID;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;
import com.rfid.scan.util.Tools;
import com.rfid.scan.util.Util;
import za.co.facegroup.zar.License;
import za.co.facegroup.zar.drivingLicenseCard.drivingLicenseCard;
public class ScanDev {

	private Decoder mDecoder ;
	private DecodeResult mDecodeResult;
	
	private boolean initFlag = false ;
	
	private String TAG = "ScanDev" ;
	
	//init device
	public boolean  initDev(){
		
		mDecoder = new Decoder();
		mDecodeResult = new DecodeResult();
		try {
			mDecoder.connectDecoderLibrary();
			initPara() ;
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			initFlag = false  ; 
			return initFlag ;
		}
		initFlag = true ; 
		return initFlag ; 
	}
	
	public void  initPara(){
		try {
			mDecoder.disableSymbology(SymbologyID.SYM_ALL) ;
			mDecoder.enableSymbology(SymbologyID.SYM_ALL);
			mDecoder.enableSymbology(SymbologyID.SYM_QR);
			mDecoder.enableSymbology(SymbologyID.SYM_PDF417);
			mDecoder.enableSymbology(SymbologyID.SYM_EAN13);
			mDecoder.enableSymbology(SymbologyID.SYM_DATAMATRIX) ;
			
//			mDecoder.enableSymbology(SymbologyID.SYM_DATAMATRIX);
//			mDecoder.enableSymbology(SymbologyID.SYM_UPCA);
//			mDecoder.enableSymbology(SymbologyID.SYM_CHINAPOST);
			
			mDecoder.enableSymbology(SymbologyID.SYM_CODE39);
			mDecoder.enableSymbology(SymbologyID.SYM_CODE128);
//			mDecoder.enableSymbology(SymbologyID.SYM_EAN8);
			mDecoder.enableSymbology(SymbologyID.SYM_CODE93);
//			mDecoder.enableSymbology(SymbologyID.SYM_INT25);
			
//			mDecoder.setOCRMode(OCRMode.OCR_BOTH) ;
//			mDecoder.setOCRTemplates(OCRTemplate.ISBN);
			
//			mDecoder.enableSymbology(SymbologyID.SYM_OCR) ;
//			mDecoder.setOCRMode(OCRMode.OCR_NORMAL_VIDEO) ;
//			mDecoder.setOCRTemplates(OCRTemplate.USER);
//			mDecoder.setOCRUserTemplate(Tools.HexString2Bytes("01010505050505050505050500")) ;
			//��EAN13��У��
			SymbologyConfig config = new SymbologyConfig(SymbologyID.SYM_EAN13);
			config.Flags = 5 ;
			config.Mask = 1 ;
			mDecoder.setSymbologyConfig(config);
			
//			mDecoder.setLightsMode(LightsMode.ILLUM_AIM_OFF);
			try {
				Thread.sleep(300);
				mDecoder.startScanning();
				Thread.sleep(100);
				mDecoder.stopScanning();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String bytesToHexString(byte[] input){
		StringBuilder sb = new StringBuilder();
		for (byte b : input){
			sb.append(String.format("%02x", b&0xff));
		}
		return sb.toString();
	}

	//scan
	public String scan() {
		String result = "" ;
		byte[] resultByte = null ;
		if(initFlag){
			try {
//				boolean is = mDecoder.callbackKeepGoing();
				Thread.sleep(50) ;
				mDecoder.waitForDecodeTwo(3000, mDecodeResult);
//				mDecoder.waitForDecode(3000) ;
				result = mDecoder.getBarcodeData() ;
				resultByte = mDecoder.getBarcodeByteData() ;
				try{
					License license = new License();
					drivingLicenseCard dlCard = new drivingLicenseCard();

					dlCard = license.deserialize(resultByte);

					String string = "";
				}catch(Exception ex) {

				}
				if(result != null){
					Log.e(TAG, result) ;
				}
//				GetLastImage() ;
//				mDecoder.get
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.getErrorCode() ;
//				if(e.getErrorCode() != 5){
					Util.writeLog("error code = " + e.getErrorCode() + ";    Error MSG: "+ e.toString()) ;
//				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result ;
	}
	
	
	////////////////////////
	////////////////////////////
	
	private void GetImage() throws DecoderException
	{		
		Bitmap bmp = null;
		
		boolean bPreview = true;
		
		// Set height width
		int height = 480;
		int width = 800;
		
		// Preview mode is image width/4 height/4
		if(bPreview)
		{
			height = height / 4;
			width = width / 4;
		}
			
		// Create bmp
		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		// Get image
		if(bPreview)
			mDecoder.getPreviewFrame(bmp);
		else
			mDecoder.getSingleFrame(bmp);
		ByteArrayOutputStream output = new ByteArrayOutputStream();//��ʼ��һ��������
		bmp.compress(CompressFormat.JPEG, 100, output) ;
		
		 bmp.recycle();//����ѡ���Ƿ���л���
	     byte[] result = output.toByteArray();//ת���ɹ�
	     if(result != null){
	    	File file = new File("/mnt/sdcard/aa.jpg") ; 
	    	
	    	try {
	    		OutputStream os = new FileOutputStream(file) ;
				os.write(result) ;
				os.flush() ;
				os.close() ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }
	     try {
			output.close() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	int imgCount = 0 ;
	
	void GetLastImage() throws DecoderException
	{
		//Log. d(TAG, "GetLastImage++");
		int g_nImageWidth = mDecoder.getImageWidth();
	   int  g_nImageHeight = mDecoder.getImageHeight();
		Bitmap bmp = Bitmap.createBitmap(g_nImageWidth, g_nImageHeight, Bitmap.Config.RGB_565);
		ImageAttributes attr = new ImageAttributes();	
		byte[] buffer = new byte[g_nImageWidth * g_nImageHeight]; // it 8-bit RAW
		
		// Get last image
		try 
		{
			buffer = mDecoder.getLastImage(attr);
		} 
		catch (DecoderException e) {
//			HandleDecoderException(e);
			return;
		}

		// Ensure it's valid
		if(buffer.equals(null))
		{
			Log. d(TAG, "no image");
			return;
		}
		
		// Convert from 8-bit RAW to 16-bit color
		int width = g_nImageWidth;
		int height = g_nImageHeight;
		int[] array = new int[width*height*2]; // 2 bytes per pixel (RGB_565)
		
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				array[width*h + w] = buffer[width*h + w] * 0x00010101;		
			}
		}
		
		// Set the pixels
		bmp.setPixels(array, 0, width, 0, 0, width, height);

		// Display image (TODO: make this a method like ShowImage)
		//DisplayImage(bmp);

		//Log. d(TAG, "GetLastImage--");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();//��ʼ��һ��������
		bmp.compress(CompressFormat.JPEG, 100, output) ;
		
		 bmp.recycle();//����ѡ���Ƿ���л���
	     byte[] result = output.toByteArray();//ת���ɹ�
	     if(result != null){
	    	File file = new File("/mnt/sdcard/aa"+ imgCount + ".jpg") ; 
	    	imgCount++;
	    	try {
	    		OutputStream os = new FileOutputStream(file) ;
				os.write(result) ;
				os.flush() ;
				os.close() ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }
	     try {
			output.close() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	/////////////////////////////////////
	
	
	//close
	public void close() {
		if(initFlag){
			try {
				mDecoder.disconnectDecoderLibrary() ;
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
