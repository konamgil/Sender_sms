package com.konamgil.sender.sender;

import android.content.Context;

/**
 * Created by konamgil on 2017-05-24.
 */

public class XmlHelper {

    private String stTimeCount, stTimeKinds, stRepeatCount, stMessage = null;
    private Context mContext;

    /**
     * 생성자 각 스트링 값 받아오기
     * @param stTimeCount
     * @param stTimeKinds
     * @param stRepeatCount
     * @param stMessage
     */
    public XmlHelper(String stTimeCount, String stTimeKinds,String stRepeatCount,String stMessage, Context context) {
        this.stTimeCount = stTimeCount.toString();
        this.stTimeKinds = stTimeKinds.toString();
        this.stRepeatCount = stRepeatCount.toString();
        this.stMessage = stMessage.toString();
        mContext = context;
    }

    /**
     * 만들어진 xml data 반환
     * @return
     */
    public String getXmlData(){
        return makingXml(stTimeCount,stTimeKinds,stRepeatCount,stMessage);
    }

    /**
     * xml 데이터 만들기
     * @param stTimeCount
     * @param stTimeKinds
     * @param stRepeatCount
     * @param stMessage
     * @return
     */
    private String makingXml(String stTimeCount, String stTimeKinds, String stRepeatCount,String stMessage){
        StringBuffer xmlStringData = new StringBuffer();
        xmlStringData
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<SMS>\n")
                .append("<TimeCount>").append(stTimeCount).append("</TimeCount>\n")
                .append("<TimeKinds>").append(stTimeKinds).append("</TimeKinds>\n")
                .append("<RepeatCount>").append(stRepeatCount).append("</RepeatCount>\n")
                .append("<Message>").append(stMessage).append("</Message>\n")
                .append("</SMS>");
        String result = String.valueOf(xmlStringData);
        return result;
    }
}
























