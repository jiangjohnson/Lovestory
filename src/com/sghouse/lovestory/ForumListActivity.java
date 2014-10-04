package com.sghouse.lovestory;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ForumListActivity extends Activity {
	ListView Mainlistview;
	ListView Sublistview;
	ForumAdapter Mainadapter;
	ForumAdapter Subadapter;
	ArrayList<ObjectItem> mainlist;
	ArrayList<ObjectItem> sublist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.forum);
	    
	    ActionBar actionbar = this.getActionBar();
	    actionbar.setDisplayHomeAsUpEnabled(true);
	    
	    
	    Mainlistview = (ListView)findViewById(R.id.listViewMain);
	    Sublistview  = (ListView)findViewById(R.id.listViewSub);
	    
	    InitializeArraylist();
	    
	    Mainadapter = new ForumAdapter(this,R.layout.list_row,mainlist);	    
	    Mainlistview.setAdapter(Mainadapter);
	    
	    Subadapter = new ForumAdapter(this,R.layout.list_row,sublist);
	    Sublistview.setAdapter(Subadapter);
	    
	    if (mainlist.size()>0)
		{
		  Mainlistview.setOnItemClickListener
		  (
				     new OnItemClickListener()
		           {
	            	 @Override
			         public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			         {
				      UpdateSubArraylist(position);	
				      Subadapter.notifyDataSetChanged();
			         }  			  
		            }
	      );
		};
	}
	
	public void InitializeArraylist()
	{	
		if (mainlist == null)
		 mainlist = new ArrayList<ObjectItem> ();
		
		mainlist.clear();
		for(int i=0;i<=10; i++)
		 {
	   	   ObjectItem o = new ObjectItem(i,-1);	   
		   mainlist.add(o);   
		 }
		
		if (sublist == null)
			sublist = new ArrayList<ObjectItem> ();		
		sublist.clear();
		ObjectItem o = new ObjectItem(0,0);	   
		sublist.add(o);   
	}
	
	public void UpdateSubArraylist(int MainPosition)
	{
	   sublist.clear();
	   for(int i=0;i<=MainPosition; i++)
	   {
   	       ObjectItem o = new ObjectItem(MainPosition,i);	   
		   sublist.add(o);   
	   }

	}
	
}

class ObjectItem {
    String Title="";
    ObjectItem(int MainItemPosition, int SubItemPosition)
    {
    	this.Title = "forum_" +Integer.toString(MainItemPosition);
    	if (SubItemPosition>0)
    		this.Title = this.Title+Integer.toString(SubItemPosition);	
    }

}


class ForumAdapter extends ArrayAdapter<ObjectItem> {

	int resourceID;
	Context mContext;
	ArrayList<ObjectItem>  itemlist ;
	
	ForumAdapter(Context mContext, int RowRecID, ArrayList<ObjectItem> list)
	{
		super(mContext, RowRecID);
		
		this.resourceID = RowRecID;
		
		this.mContext   = mContext;
		
		this.itemlist   = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemlist.size();
	}

	@Override
	public ObjectItem getItem(int position) {
		// TODO Auto-generated method stub
		
		return itemlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int Postion, View view, ViewGroup container) {
		if (view == null)
		{
			LayoutInflater inflater = ((Activity) this.mContext).getLayoutInflater();
			view = inflater.inflate(resourceID, container, false);			
		}
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(itemlist.get(Postion).Title);
			
		return view;
	}
	
}

	
	
	


