package com.quickblox.supersamples.main.helpers;

import java.util.ArrayList;
import java.util.List;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.objects.ChatItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<ChatItem>{
	private Context ctx;
	private List<ChatItem> items;
	private List<String> ids;
	private List<String> messages;
	
	public ChatArrayAdapter(Context context, int textViewResourceId,
			List<ChatItem> objects) {
		super(context, textViewResourceId, objects);
		
		ctx = context;
		items = objects;
		
		ids = new ArrayList<String>();
		messages = new ArrayList<String>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = convertView;
		if(rowView == null){
			rowView = inflater.inflate(R.layout.chat_listview_item, null);
		}
		
		// set Username
		TextView userName = (TextView) rowView.findViewById(R.id.chat_message_header);
		String text = String.format("On %s %s wrote:", items.get(position).getDate(), 
				items.get(position).getUserName());
		userName.setText(text);
		
		// set Status
		TextView messageDatePost = (TextView) rowView.findViewById(R.id.chat_message);
		messageDatePost.setText(items.get(position).getMessage());
		
		return rowView;
	}
	
	public void add (ChatItem object){
		super.add(object);
		ids.add(object.getID());
		messages.add(object.getMessage());
	}
	
	public void insert (ChatItem object, int index){
		super.insert(object, index);
		ids.add(object.getID());
		messages.add(object.getMessage());
	}
	
	public void remove (ChatItem object){
		super.remove(object);
		ids.remove(object.getID());
		messages.remove(object.getMessage());
	}
	
	public boolean isHasID(String elementID){
		if(ids.contains(elementID)){
			return true;
		}
		return false;
	}
	public boolean isHasMessage(String elementMessage){
		if(messages.contains(elementMessage)){
			return true;
		}
		return false;
	}
}