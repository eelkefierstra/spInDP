package com.example.aldert.spindpappandroidstudio22;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

/**
 * Created by Aldert on 31-5-2016.
 */

public class XMLreader{
    Servo servo[] = new Servo[19];
    int[] hellingInfo = new int[2];
    int id;
    int hoek;
    int temperatuur;
    int count = 0;
    public XMLreader(String input){
        if(input.length() >20){
            String woord = "";
            String tag = "";
            boolean endtag = false;
            for(char c : input.toCharArray()){
                switch (c){
                    case '<':
                        if(!endtag){
                            if(tag.equals("Id")){
                                id = Integer.parseInt(woord);
                            }
                            else if(tag.equals("Hoek")){
                                hoek = Integer.parseInt(woord);
                            }
                            else if(tag.equals("Temperatuur")){
                                temperatuur = Integer.parseInt(woord);
                            }
                            else if(tag.equals("Servo")){
                                servo[count] = new Servo(id, hoek, temperatuur);
                                count++;
                            }
                        }
                        woord = "";
                        endtag = false;
                        break;
                    case '>':
                        tag = woord;
                        woord = "";
                        break;
                    case '/':
                        tag = woord;
                        woord = "";
                        endtag = true;
                        break;
                    default:
                        woord += c;
                        break;
                }
            }
            servo[18] =  new Servo(id, hoek, temperatuur);
        }
        else if(input.length() <20){
            String woord = "";
            for(char c : input.toCharArray()){
                switch (c){
                    case '<':
                        break;
                    case '>':
                        hellingInfo[1] = Integer.parseInt(woord);
                        woord = "";
                        break;
                    case ':':
                        break;
                    case 'X':
                        break;
                    case 'Y':
                        break;
                    case ',':
                        hellingInfo[0] = Integer.parseInt(woord);
                        woord = "";
                        break;
                    default:
                        woord += c;
                        break;
                }
            }
        }

    }

    public Servo[] getServo(){
        return this.servo;
    }/**/

    public int[] getHellingInfo(){
        return this.hellingInfo;
    }

/*
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }

    }
    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "Servo");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Id")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Servo readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        //parser.require(XmlPullParser.START_TAG, ns, "entry");
        int Id = -1;
        String Hoek = null;
        String Temperatuur = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Id")) {
                Id = readId(parser);
            } else if (name.equals("Hoek")) {
                Hoek = readHoek(parser);
            } else if (name.equals("Temperatuur")) {
                Temperatuur = readTemperatuur(parser);
            } else {
                skip(parser);
            }
        }
        return new Servo(Id, Hoek, Temperatuur);
    }

    // Processes title tags in the feed.
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Id");
        int title = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "Id");
        return title;
    }

    // Processes link tags in the feed.
    private String readTemperatuur(XmlPullParser parser) throws IOException, XmlPullParserException {
        String Temperatuur = "";
        parser.require(XmlPullParser.START_TAG, ns, "Temperatuur");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                Temperatuur = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return Temperatuur;
    }

    // Processes summary tags in the feed.
    private String readHoek(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Hoek");
        String Hoek = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Hoek");
        return Hoek;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
/**/

}
