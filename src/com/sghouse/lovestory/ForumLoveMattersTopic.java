package com.sghouse.lovestory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class ForumLoveMattersTopic 
{	 
    private String topicid,topicbrief,author,replies,viewers;
 
    private String imgUrl;
 
    private Bitmap image;
 
    private ForumLoveMattersTopicAdapter sta;
 
    public ForumLoveMattersTopic(String topicid, String topicbrief, String author, String replies, String viewers, String imgUrl) 
    {
    	this.topicid    = topicid;
    	this.topicbrief = topicbrief;
    	this.author     = author;
    	this.replies    = replies + " replies";
    	this.viewers    = viewers + " viewers";
    	this.imgUrl     = imgUrl;
    	
    	// TO BE LOADED LATER - OR CAN SET TO A DEFAULT IMAGE
    	this.image = null;
    }
    
    public String getTopicid() 
    {
        return this.topicid;
    }
    
  
    public String getTopicBrief() 
    {
        return this.topicbrief;
    }
    
    public void SetTopicBrief(String topicbrief) 
    {
        this.topicbrief = topicbrief;
    }
    
    public String getAuthor() 
    {
        return this.author;
    }
 
    public void setAuthor(String author) 
    {
        this.author = author;
    }
    public String getReplies() 
    {
        return this.replies;
    }
 
    public void setReplies(String replies) 
    {
        this.replies = replies;
    }
    public String getViewers() 
    {
        return viewers;
    }
 
    public void setName(String viewers) 
    {
        this.viewers = viewers;
    }
 
    public String getImgUrl() {
        return imgUrl;
    }
 
    public void setImgUrl(String imgUrl) 
    {
        this.imgUrl = imgUrl;
    }
 
    public Bitmap getImage() 
    {
        return image;
    }
 
    public ForumLoveMattersTopicAdapter getAdapter() 
    {
        return sta;
    }
 
    public void setAdapter(ForumLoveMattersTopicAdapter sta) 
    {
        this.sta = sta;
    }
 
    public void loadImage(ForumLoveMattersTopicAdapter sta) 
    {
        // HOLD A REFERENCE TO THE ADAPTER
        this.sta = sta;
        if (imgUrl != null && !imgUrl.equals("")) 
        {
            new ImageLoadTask().execute(imgUrl);
        }
    }
    
 
    // ASYNC TASK TO AVOID CHOKING UP UI THREAD
    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> 
    {
 
        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }
 
        // PARAM[0] IS IMG URL
        protected Bitmap doInBackground(String... param) {
            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
            try {
                Bitmap b = getBitmapFromURL(param[0]);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        protected Bitmap getBitmapFromURL(String src) 
        {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
 
        protected void onProgressUpdate(String... progress) {
            // NO OP
        }
 
        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded " + topicbrief + " image");
                image = ret;
                if (sta != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                    sta.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load " + topicbrief + " image");
            }
        }
    }  
    
 
}


class ForumLoveMattersViewHolder 
{
	TextView TopicId;
    TextView TopicBrief;
    TextView Author;
    TextView Replies;
    TextView Viewers;   
    ImageView icon;
}

class ForumLoveMattersTopicAdapter extends BaseAdapter 
{
  	 
    private LayoutInflater mInflater;
 
    private ArrayList<ForumLoveMattersTopic> items;
 
    public ForumLoveMattersTopicAdapter(Context context, ArrayList<ForumLoveMattersTopic> items) 
    {
        mInflater = LayoutInflater.from(context);
        this.items = items;
    }
 
    public int getCount() {
        return items.size();
    }
 
    public ForumLoveMattersTopic getItem(int position) 
    {
        return items.get(position);
    }
 
    public long getItemId(int position) 
    {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	ForumLoveMattersViewHolder holder;
        ForumLoveMattersTopic s = items.get(position);
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.forum_lovematters_rowitem, null);
            holder = new ForumLoveMattersViewHolder();
            holder.TopicId    = (TextView) convertView.findViewById(R.id.textView_forum_lovematters_topicID);
            holder.TopicBrief = (TextView) convertView.findViewById(R.id.textView_forum_lovematters_topicbrief);
            holder.Author     = (TextView) convertView.findViewById(R.id.textView_forum_lovematters_Author);
            holder.Replies    = (TextView) convertView.findViewById(R.id.textView_forum_lovematters_Replies);
            holder.Viewers    = (TextView) convertView.findViewById(R.id.textView_forum_lovematters_Viewers);
            //holder.icon = (ImageView) convertView.findViewById(R.id.);
            convertView.setTag(holder);
        } else 
        {
            holder = (ForumLoveMattersViewHolder) convertView.getTag();
        }
        holder.TopicId.setText(s.getTopicid());
        holder.TopicBrief.setText(s.getTopicBrief());
        holder.Author.setText(s.getAuthor());
        holder.Replies.setText(s.getReplies());
        holder.Viewers.setText(s.getViewers());
        
        if (s.getImage() != null) {
            holder.icon.setImageBitmap(s.getImage());
        } else 
        {
                // MY DEFAULT IMAGE
            holder.icon.setImageResource(R.drawable.icon);
        }
        return convertView;
    }
 
}

