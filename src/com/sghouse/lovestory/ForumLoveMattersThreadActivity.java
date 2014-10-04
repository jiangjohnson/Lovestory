package com.sghouse.lovestory;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class ForumLoveMattersThreadActivity extends Activity {

	private String topicid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.forum_lovematters_rowitemdetails);
	    
	    ActionBar actionbar = this.getActionBar();
	    actionbar.setDisplayHomeAsUpEnabled(true);	
	    
	   //((GlobalData)this.getApplication()).getForumLovemattersTopicId();

	    topicid = this.getIntent().getStringExtra(GlobalData.FORUM_LOVEMATTERS_TOPICID);
	    //load COMMENTS base on topicid
	    
	    
	}
	
}
