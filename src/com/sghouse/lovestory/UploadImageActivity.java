package com.sghouse.lovestory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class UploadImageActivity extends Activity 
{ 	
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CONTENT_IMAGE_ACTIVITY_REQUEST_CODE = 10;
	//private Uri selectedImageUri;
	private Uri fileUri;
	private ImageView preview;
	private TextView messageText;
	private Button uploadButton, btnselectpic;
	private int serverResponseCode = 0;
	private ProgressDialog dialog = null;
	Bitmap bitmap;
	
	//private String upLoadServerUri = null;
	private String imagepath=null;
	private String upLoadServerUrl= "http://sghouse.net/androidphpscripts/uploadtoserver.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageupload);
		
		ActionBar actionbar = this.getActionBar();
	    actionbar.setDisplayHomeAsUpEnabled(true);
		
		Button bGallery = (Button) findViewById(R.id.bgallery);
		Button bCamera= (Button) findViewById(R.id.bcamera);
		Button bUpload=(Button) findViewById(R.id.buploadimage);
		preview = (ImageView) findViewById(R.id.imageViewUpload);
		messageText = (TextView)findViewById(R.id.messageText);
		bCamera.setOnClickListener(
				new OnClickListener() 
				{  	
					@Override
					public void onClick(View v) 
					{
						// TODO Auto-generated method stub
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 	
						if (cameraIntent.resolveActivity(getPackageManager()) != null)
						{
							fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name							
							startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); 
						}
					}
				}
				);
		
		
		bGallery.setOnClickListener(
				new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Intent intent = new Intent();						
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_PICK);						
						startActivityForResult(Intent.createChooser(intent,"Select file to upload "), CONTENT_IMAGE_ACTIVITY_REQUEST_CODE);
						
					}
				}
				);
		
		bUpload.setOnClickListener(
				new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{	
						Thread thread = new Thread(new Runnable(){
						    @Override
						    public void run() {
						        try {
						        	 uploadFile();
						        } catch (Exception e) {
						            e.printStackTrace();
						        }
						    }
						});

						thread.start(); 						
					}
				}
				);
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{   
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) 
			{
				
				if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) 
				{  
				    			     
				    imagepath   = (new File(fileUri.toString())).getAbsolutePath();
					preview.setImageURI(fileUri);
					Log.d("selectedPath1 : " ,imagepath);
					
				} 
				
				if (requestCode == CONTENT_IMAGE_ACTIVITY_REQUEST_CODE)					
				{
					if(data.getData() != null)
					{
						fileUri = data.getData();
						Toast.makeText(this, "Image saved to:\n" +
			                     data.getData(), Toast.LENGTH_LONG).show();
					}else
					{
						Log.d("selectedPath1 : ","Came here its null !");
						Toast.makeText(getApplicationContext(), "failed to get Image!", 500).show();
					}
					imagepath = (new File(fileUri.toString())).getAbsolutePath();
					preview.setImageURI(fileUri);
					Log.d("selectedPath1 : " ,imagepath);
					filesize(fileUri);
				}
				
			}
		}
		catch (Exception e)
		{
			 e.printStackTrace();
		}
	
	}
	
	
	public String getPath(Uri uri) 
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = this.getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    String path= cursor.getString(column_index);
	    cursor.close();
	    return path;
	}
	
	public void filesize(Uri uri)
	{
		String path = getPath(uri);
		bitmap = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
		messageText.setText(path+"----"+Integer.toString(bitmap.getByteCount()));
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
	
	public int uploadFile() 
	{
		String sourceFileUri="";
		if (fileUri.getScheme().toString().compareTo("content")==0)
			sourceFileUri = getPath(fileUri); 
     	if (fileUri.getScheme().toString().compareTo("file")==0)
     		sourceFileUri = fileUri.getPath();
		decodeSampledBitmapFromResource(sourceFileUri,240,400);
        String fileName = generatefilename(); 
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
        	URL url = new URL(upLoadServerUrl);
        	
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
        	
        	runOnUiThread(new Runnable() 
        	{
        		public void run() 
        		{
        			messageText.setText("MalformedURLException Exception : check script url.");
        			Toast.makeText(UploadImageActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
        		}
        	});
        	
        	Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
        } catch (Exception e) 
        {
        	
        	dialog.dismiss();  
        	e.printStackTrace();
        	
        	runOnUiThread(new Runnable() {
        		public void run() {
        			messageText.setText("Got Exception : see logcat ");
        			Toast.makeText(UploadImageActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
        		}
        	});
        	Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
        }
        dialog.dismiss();       
        return serverResponseCode; 
        
	}
	
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	
	
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
	    Log.d("TimeStamp", timeStamp);
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	private static String generatefilename()
	{		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));		
		return "IMG_"+ timeStamp + ".jpg";		
	}
	
	
}
