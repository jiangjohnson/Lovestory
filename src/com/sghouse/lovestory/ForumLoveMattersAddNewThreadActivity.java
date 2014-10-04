package com.sghouse.lovestory;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ForumLoveMattersAddNewThreadActivity extends Activity {

	//ArrayList<String> SelectedImages = new ArrayList<String>();
	Uri fileUri;
	private ActionMode.Callback mActionModeCallback;
	ActionMode    mActionMode;
	View          clickedimageview;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.forum_lovematters_new_thread);
	    
	    ImageButton bCamera = (ImageButton) this.findViewById(R.id.imageButton_forum_lovematters_cameraimage);	    

	    ImageButton bGallery = (ImageButton) this.findViewById(R.id.imageButton_forum_lovematters_gallery);
	    
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
							fileUri = GlobalTools.getOutputMediaFileUri(GlobalData.MEDIA_TYPE_IMAGE); // create a file to save the image
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name							
							startActivityForResult(cameraIntent, GlobalData.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); 
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
						/*Intent intent = new Intent();						
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_PICK);
						startActivityForResult(Intent.createChooser(intent,"Select file to upload "), CONTENT_IMAGE_ACTIVITY_REQUEST_CODE);*/
						Intent intent = new Intent();
						intent.putExtra(GlobalData.HOW_MANY_IMAGES_TO_ADD, 3-SelectedImages.size());
						startActivityForResult(intent, GlobalData.CONTENT_IMAGE_ACTIVITY_REQUEST_CODE);						
					}
				}
				);
	    
	    
	    
	    Button bsubmit = (Button) findViewById(R.id.button_imageButton_forum_lovematters_submission);
	    bsubmit.setOnClickListener(
				new OnClickListener() 
				{  	
					@Override
					public void onClick(View v) 
					{						
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
	    
	    
	        mActionModeCallback = new ActionMode.Callback() 
	        {
	        	// Called when the action mode is created; startActionMode() was called
	        	@Override
	        	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        		// Inflate a menu resource providing context menu items
	        		MenuInflater inflater = mode.getMenuInflater();
	        		inflater.inflate(R.menu.imageactions, menu);
	        		return true;
	        	}
	        	
	        	// Called each time the action mode is shown. Always called after onCreateActionMode, but
	        	// may be called multiple times if the mode is invalidated.
	        	@Override
	        	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        		return false; // Return false if nothing is done
	        	}
	        	
	        	// Called when the user selects a contextual menu item
	        	@Override
	        	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        		switch (item.getItemId()) {
	        		case R.id.action_deleteimage:
	        			deleteCurrentImage();
	        			mode.finish(); // Action picked, so close the CAB
	        			return true;
	        		default:
	        			return false;
	        		}
	        	}
	        	
	        	// Called when the user exits the action mode
	        	@Override
	        	public void onDestroyActionMode(ActionMode mode) {
	        		mActionMode = null;
	        	}			

	       };
	    
	       ImageView imageview = (ImageView) this.findViewById(R.id.imageView_forum_lovematters_image1); 
	       imageview.setOnLongClickListener(new View.OnLongClickListener() 
	       {
	    	    // Called when the user long-clicks on someView
	    	    public boolean onLongClick(View view) 
	    	    {
	    	        if (mActionMode != null) 
	    	        {
	    	            return false;  
	    	        }

	    	        // Start the CAB using the ActionMode.Callback defined above
	    	        mActionMode = ForumLoveMattersAddNewThreadActivity.this.startActionMode(mActionModeCallback); 
	    	        view.setSelected(true);  
	    	        clickedimageview = view;
	    	        return true;
	    	    }
	    	});
	       
	       
	    
	}
	
	private void deleteCurrentImage()
	{
		if ((clickedimageview != null) && (clickedimageview instanceof ImageView))
		{
			ImageView imageview = ((ImageView)clickedimageview);			
			imageview.setImageResource(0);
			imageview.setVisibility(View.GONE);
			imageview.setTag(null);		
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{	
		try
		{   
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK)  
			{
				Uri imageUri ;
				imageUri = data.getData();
			 	if(imageUri != null)
				{
			 		if (requestCode == GlobalData.CONTENT_IMAGE_ACTIVITY_REQUEST_CODE)
			 		{
			 			 ArrayList<String> galleryphotos = data.getStringArrayListExtra(GlobalData.CONTENT_IMAGE_MULTIPLE_SELECTIONS);
						 for(String s : galleryphotos )
						 {
							 ResetImageView(s);
						 }
			 		}
			 		else if (requestCode == GlobalData.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
			 		{
			 			ResetImageView(imageUri.getPath());
			 		}
				 
				}				
			}
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}
		
	}
	
	protected void ResetImageView(String path)
	{
		ImageView imageview1 = (ImageView) this.findViewById(R.id.imageView_forum_lovematters_image1);
		if (imageview1.getVisibility() == View.GONE)
		 {
			imageview1.setVisibility(View.VISIBLE);
			imageview1.setImageBitmap(BitmapFactory.decodeFile(path));
			imageview1.setTag(path);
		 }	
		
		ImageView imageview2 = (ImageView) this.findViewById(R.id.imageView_forum_lovematters_image2);
		if (imageview2.getVisibility() == View.GONE)
		 {
			imageview2.setVisibility(View.VISIBLE);
			imageview2.setImageBitmap(BitmapFactory.decodeFile(path));
			imageview2.setTag(path);
		 }	
		
		ImageView imageview3 = (ImageView) this.findViewById(R.id.imageView_forum_lovematters_image3);
		if (imageview3.getVisibility() == View.GONE)
		 {
			imageview3.setVisibility(View.VISIBLE);
			imageview3.setImageBitmap(BitmapFactory.decodeFile(path));
			imageview3.setTag(path);
		 }	
	}	
	
}
