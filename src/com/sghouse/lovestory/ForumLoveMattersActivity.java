package com.sghouse.lovestory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class ForumLoveMattersActivity  extends ListActivity {
	
	 private ArrayList<ForumLoveMattersTopic> topics;
     private ForumLoveMattersTopicAdapter sta;
     
     JSONParser Jparser = new JSONParser();
     private static String url_all_forum_lovematters_topics = "http://sghouse.net/androidphpscripts/get_all_forum_lovematters_topics.php";
     private static final String TAG_SUCCESS  = "success";
     private static final String TAG_TOPICS = "topics";
     private static final String TAG_TOPICID = "topicid";
     private static final String TAG_TOPICBRIEF = "topicbrief";
     private static final String TAG_AUTHOR = "author";
     private static final String TAG_REPLIES = "replies";
     private static final String TAG_VIEWERS = "viewers";
     
     JSONArray temptopics = null; 

     @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.forum_lovematters_mainpage);
        
	    ActionBar actionbar = this.getActionBar();
	    actionbar.setDisplayHomeAsUpEnabled(true);	
	    
	    //FORUM_LOVEMATTERS_TOPICID = ((GlobalData)this.getApplication()).getForumLovemattersTopicId();
	    
	    ListView lv = this.getListView();
	    // GET YOUR TOPICS
	    new LoadAllForumLoveMattersTopics().execute();

	    // CREATE BASE ADAPTER
		sta = new ForumLoveMattersTopicAdapter(this, topics);
        // SET AS CURRENT LIST
        setListAdapter(sta);

        lv.setOnItemClickListener(new OnItemClickListener() 
        {
			@Override
			public void onItemClick(AdapterView<?> arg0,
					View view, int arg2, long arg3)
			{
				TextView TVtopicID = (TextView) view.findViewById(R.id.textView_forum_lovematters_topicID);
				String topicID     = TVtopicID.getText().toString();
				Intent  i          = new Intent(getApplicationContext(),ForumLoveMattersThreadActivity.class);
				i.putExtra(GlobalData.FORUM_LOVEMATTERS_TOPICID, topicID);
				Log.d("Json",  "topicpid="+topicID);
				startActivity(i);
			}
        });		
 
        
        /*for (ForumLoveMattersTopic s : topics) 
        {
                // START LOADING IMAGES FOR EACH STUDENT
                s.loadImage(sta);
        }*/
        
	    
	}
	
	
	
	class LoadAllForumLoveMattersTopics extends AsyncTask<String, String, String>
	{
	    
		ProgressDialog pDialog;
		
		@Override
	    protected void onPreExecute() 
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(ForumLoveMattersActivity.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();				
		}
		
		@Override
		protected String doInBackground(String... arg0) 
		{
			
			List <NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = Jparser.makeHttpRequest(url_all_forum_lovematters_topics, "GET", params);
			Log.d("All lovematter topics", json.toString());
			try
			{
				int success = json.getInt(TAG_SUCCESS);
				if(success ==1)
				{
					temptopics = json.getJSONArray(TAG_TOPICS);
					for(int i = 0; i<temptopics.length(); i++)
					{						
						JSONObject row = temptopics.getJSONObject(i);
						String topicid = row.getString(TAG_TOPICID);
						String topicbrief= row.getString(TAG_TOPICBRIEF);
						String author = row.getString(TAG_AUTHOR);
						String replies= row.getString(TAG_REPLIES);
						String viewers= row.getString(TAG_VIEWERS);						
						ForumLoveMattersTopic topic = new ForumLoveMattersTopic(topicid, topicbrief, author, replies, viewers,"");						
						topics.add(topic);					
					}
				
				} else
				{   //no topics, then turn to add new thread page.
					Intent i= new Intent(getApplicationContext(),ForumLoveMattersAddNewThreadActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
				
			}catch (JSONException e) 
			{
	            e.printStackTrace();
	        }
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url)
		{
		  pDialog.dismiss();
		  sta.notifyDataSetChanged();			
		}
	}		

}



