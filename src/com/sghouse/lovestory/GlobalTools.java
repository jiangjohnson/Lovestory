package com.sghouse.lovestory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GlobalTools 
{
	public String getPath(Uri uri, Context applicationcontext) 
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = applicationcontext.getContentResolver().query(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    String path= cursor.getString(column_index);
	    cursor.close();
	    return path;
	}
	
	public void filesize(Uri uri, Context applicationcontext)
	{
		String path = getPath(uri,applicationcontext);
		Bitmap bitmap = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
		//messageText.setText(path+"----"+Integer.toString(bitmap.getByteCount()));
	}
	
	public static int calculateInSampleSize(int orgWidth, int orgHeight, int reqWidth, int reqHeight) 
	{
		// Raw height and width of image
		final int height = orgHeight;
		final int width = orgWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) 
		{
			
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) 
			{
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}
	
	
	public void decodeSampledBitmapFromResource(String sourceFileUri,int reqWidth, int reqHeight)   
	{
		Bitmap bitmap;
		
		// First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    bitmap = BitmapFactory.decodeFile(sourceFileUri, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options.outWidth,options.outHeight, reqWidth,  reqHeight);
	    if (options.inSampleSize<2)
	      options.inSampleSize =2;
	    
	    options.inJustDecodeBounds = false;
	    bitmap = BitmapFactory.decodeFile(sourceFileUri, options);
	    	   
	    try
	    {
	    	File compressedPictureFile = new File(sourceFileUri);	    
		    FileOutputStream fOut = new FileOutputStream(compressedPictureFile);
		    boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		    fOut.flush();
		    fOut.close();
	    }
	    catch(FileNotFoundException e)
	    {
	    	e.printStackTrace();
	    }
	    catch(IOException e)
	    {
	    	e.printStackTrace();
	    }
	    
	}
	
	public int uploadFile(String sourceFileUri, Context applicationcontext, String upLoadServerPhpScriptFileUrl,ProgressDialog dialog) 
	{		
		decodeSampledBitmapFromResource(sourceFileUri,240,400);
        String fileName = generateimagefilename(); 
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        try { 
        	  
        	FileInputStream fileInputStream = new FileInputStream(sourceFile);
        	URL url = new URL(upLoadServerPhpScriptFileUrl);
        	
        	// Open a HTTP  connection to  the URL
        	conn = (HttpURLConnection) url.openConnection(); 
        	conn.setDoInput(true); // Allow Inputs
        	conn.setDoOutput(true); // Allow Outputs
        	conn.setUseCaches(false); // Don't use a Cached Copy
        	conn.setRequestMethod("POST");
        	conn.setRequestProperty("Connection", "Keep-Alive");
        	conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        	conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        	conn.setRequestProperty("uploaded_file", fileName); 
        	
        	dos = new DataOutputStream(conn.getOutputStream());
        	
        	dos.writeBytes(twoHyphens + boundary + lineEnd); 
        	dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
        	
        	dos.writeBytes(lineEnd);
        	
        	// create a buffer of  maximum size
        	bytesAvailable = fileInputStream.available(); 
        	
        	bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	buffer = new byte[bufferSize];
        	
        	// read file and write it into form...
        	bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
        	
        	while (bytesRead > 0) {
        		
        		dos.write(buffer, 0, bufferSize);
        		bytesAvailable = fileInputStream.available();
        		bufferSize = Math.min(bytesAvailable, maxBufferSize);
        		bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
        		
        	}
        	
        	// send multipart form data necesssary after file data...
        	dos.writeBytes(lineEnd);
        	dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        	
        	 //serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
        	
        	//close the streams //
        	fileInputStream.close();
        	dos.flush();
        	dos.close();
        	
        } catch (MalformedURLException ex) 
        {
        	
        	dialog.dismiss();  
        	ex.printStackTrace();
        	Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
        } catch (Exception e) 
        {
        	
        	dialog.dismiss();  
        	e.printStackTrace();
        	Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
        }
        dialog.dismiss();       
        return 0;        
	}
	
	public static Uri getOutputMediaFileUri(int type)
	{
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LoveStoryCameraPhotos");	   

		if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("LoveStoryCameraPhotos", "failed to create directory");
	            return null;
	        }
	    }

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
	    Log.d("TimeStamp", timeStamp);
	    File mediaFile;
	    if (type == GlobalData.MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == GlobalData.MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return Uri.fromFile(mediaFile);
	}
	
	
	public static String generateimagefilename()
	{		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));		
		return "IMG_"+ timeStamp + ".jpg";		
	}
	

}
