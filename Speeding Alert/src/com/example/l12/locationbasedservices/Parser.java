package com.example.l12.locationbasedservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class Parser {

		private static final String ns = null;
		protected String speedLimit;
		   
	    public List<String> parse(InputStream in) throws XmlPullParserException, IOException {
	        try {
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in, null);
	            parser.nextTag();
	            return readMarkers(parser);
	        } finally {
	            in.close();
	        }
	    }
	    
	    private List<String> readMarkers(XmlPullParser parser) throws XmlPullParserException, IOException {
	        List<String> mph = new ArrayList<String>();

	        parser.require(XmlPullParser.START_TAG, ns, "markers");
	        while (parser.next() != XmlPullParser.END_TAG) {
	            if (parser.getEventType() != XmlPullParser.START_TAG) {
	                continue;
	            }
	            String name = parser.getName();
	           
	            if (name.equals("marker")) {
	            	speedLimit = parser.getAttributeValue(null, "mph");
	            	Log.d("speed limit", speedLimit);
	                parser.nextTag();
	                mph.add(speedLimit);
	            } else {
	                parser.next();
	            }
	        }  
	        return mph;
	    }

}

