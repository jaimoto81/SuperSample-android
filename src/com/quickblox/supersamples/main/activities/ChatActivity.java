package com.quickblox.supersamples.main.activities;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.helpers.LocationsXMLHandler;
import com.quickblox.supersamples.sdk.objects.LocationsList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Chat's tab");
        setContentView(textview);
    }

}
