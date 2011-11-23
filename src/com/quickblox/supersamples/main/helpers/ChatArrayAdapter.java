package com.quickblox.supersamples.main.helpers;

import java.util.List;
import java.util.Map;

import com.quickblox.supersamples.R;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<XMLNode>{
	private Context ctx;
	private List<XMLNode> items;
	
	public ChatArrayAdapter(Context context, int textViewResourceId,
			List<XMLNode> objects) {
		super(context, textViewResourceId, objects);
		
		ctx = context;
		items = objects;
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
		String text = String.format("On %s %s wrote:", items.get(position).findChild("created-at").getText(), 
				items.get(position).findChild("user-id").getText());
		userName.setText(text);
		
		// set Status
		TextView messageDatePost = (TextView) rowView.findViewById(R.id.chat_message);
		messageDatePost.setText(items.get(position).findChild("status").getText());
		
		return rowView;
	}
}
