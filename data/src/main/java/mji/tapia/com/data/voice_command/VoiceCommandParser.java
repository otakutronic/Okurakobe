package mji.tapia.com.data.voice_command;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import mji.tapia.com.data.R;

/**
 * Created by Sami on 2/8/2018.
 */

public class VoiceCommandParser {

    private Resources resources;
    public VoiceCommandParser(Resources resources) {
        this.resources = resources;
    }

   VoiceCommandDocument parseXML(int resourceId) {
        VoiceCommandDocument voiceCommandDocument = new VoiceCommandDocument();
        XmlResourceParser xmlResourceParser = resources.getXml(resourceId);
        try {
            xmlResourceParser.next();
            int eventType = xmlResourceParser.getEventType();
            VoiceCommand currentVoiceCommand = null;
            VoiceCommandEnum currentVoiceCommandEnum = null;
            Stack<VoiceCommandItem> voiceCommandItems = new Stack<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xmlResourceParser.getName()) {
                        case "command":
                            currentVoiceCommand = new VoiceCommand(xmlResourceParser.getAttributeValue(null, "id"));
                            break;
                        case "keyword":
                            if(xmlResourceParser.next() == XmlPullParser.TEXT) {
                                VoiceCommandKeyword voiceCommandKeyword = new VoiceCommandKeyword(xmlResourceParser.getText());
                                if(!voiceCommandItems.empty()){
                                    voiceCommandItems.push(voiceCommandKeyword);
                                } else {
                                    throw new RuntimeException("Keyword should be part of a group");
                                }
                            } else {
                                throw new RuntimeException("Keyword should only contain text");
                            }
                            break;
                        case "group":
                            VoiceCommandGroup voiceCommandGroup = new VoiceCommandGroup();
                            voiceCommandItems.push(voiceCommandGroup);
                            break;
                        case "group-dependent":
                            VoiceCommandGroupDependent voiceCommandGroupDependent = new VoiceCommandGroupDependent();
                            voiceCommandItems.push(voiceCommandGroupDependent);
                            break;
                        case "group-param":

                            String pos = xmlResourceParser.getAttributeValue(null, "position");
                            VoiceCommandGroupParam.Position position;
                            switch (pos) {
                                case "before":
                                    position = VoiceCommandGroupParam.Position.BEFORE;
                                    break;
                                case "after":
                                    position = VoiceCommandGroupParam.Position.AFTER;
                                    break;
                                default:
                                    position = null;
                                    break;
                            }
                            VoiceCommandGroupParam voiceCommandGroupParam = new VoiceCommandGroupParam(position, xmlResourceParser.getAttributeValue(null, "target"));
                            voiceCommandItems.push(voiceCommandGroupParam);
                            break;
                        case "parameter":
                            String id = xmlResourceParser.getAttributeValue(null, "id");
                            String type = xmlResourceParser.getAttributeValue(null, "type");
                            VoiceCommandParameter voiceCommandParameter;
                            switch (type) {
                                case "number":
                                    voiceCommandParameter = new VoiceCommandParameter(id, VoiceCommandParameter.Type.NUMBER);
                                    break;
                                case "name":
                                    voiceCommandParameter = new VoiceCommandParameter(id, VoiceCommandParameter.Type.NAME);
                                    break;
                                case "enum":
                                    String enum_str = xmlResourceParser.getAttributeValue(null, "enum");
                                    voiceCommandParameter = new VoiceCommandParameter(id, VoiceCommandParameter.Type.ENUM, enum_str);
                                    break;
                                default:
                                    throw new RuntimeException("wrong attribute for parameter");
                            }
                            if(currentVoiceCommand != null)
                                currentVoiceCommand.addParam(voiceCommandParameter);
                            else
                                throw new RuntimeException("paramter should be part of a command");
                            break;
                        case "enum":
                            if(currentVoiceCommandEnum != null || currentVoiceCommand != null || !voiceCommandItems.empty()) {
                                throw new RuntimeException("enum should be at the base of the document");
                            } else {
                                currentVoiceCommandEnum = new VoiceCommandEnum(xmlResourceParser.getAttributeValue(null,"id"));
                            }
                            break;
                        case "item":
                            if(xmlResourceParser.next() == XmlPullParser.TEXT) {
                                if(currentVoiceCommandEnum == null) {
                                    throw new RuntimeException("item should only be part of enum");
                                } else {
                                    currentVoiceCommandEnum.addEnumItem(xmlResourceParser.getText());
                                }
                            } else {
                                throw new RuntimeException("item should contain text");
                            }
                            break;
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    VoiceCommandItem voiceCommandItem;
                    switch (xmlResourceParser.getName()) {
                        case "command":
                            voiceCommandDocument.addCommand(currentVoiceCommand);
                            currentVoiceCommand = null;
                            break;
                        case "keyword":
                            voiceCommandItem = voiceCommandItems.pop();
                            if(voiceCommandItems.empty()) {
                               throw  new RuntimeException("keyword should be part of one group");
                            } else {
                                if(voiceCommandItems.peek() instanceof VoiceCommandGroup) {
                                    ((VoiceCommandGroup)voiceCommandItems.peek()).groupItems.add(voiceCommandItem);
                                } else {
                                    throw new RuntimeException("keyword should be part of a group");
                                }
                            }
                            break;
                        case "group":
                            handleEndGroup(voiceCommandItems,currentVoiceCommand);
                            break;
                        case "group-dependent":
                            handleEndGroup(voiceCommandItems,currentVoiceCommand);
                            break;
                        case "group-param":
                            handleEndGroup(voiceCommandItems,currentVoiceCommand);
                            break;
                        case "enum":
                            voiceCommandDocument.addEnum(currentVoiceCommandEnum);
                            currentVoiceCommandEnum = null;
                            break;
                    }
                }
                eventType = xmlResourceParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(voiceCommandDocument.check()) {
            return voiceCommandDocument;
        } else {
            throw new RuntimeException("inconsistent document");
        }

    }

    private void handleEndGroup(Stack<VoiceCommandItem> voiceCommandItems, VoiceCommand currentVoiceCommand) {
        VoiceCommandItem voiceCommandItem = voiceCommandItems.pop();
        if(voiceCommandItems.empty()) {
            if(currentVoiceCommand != null)
                currentVoiceCommand.addItem(voiceCommandItem);
            else
                throw new RuntimeException("group should be part of a command");
        } else {
            if(voiceCommandItems.peek() instanceof VoiceCommandGroup) {
                ((VoiceCommandGroup)voiceCommandItems.peek()).groupItems.add(voiceCommandItem);
            } else {
                throw new RuntimeException("group should be part of a group or a ");
            }
        }
    }

}
