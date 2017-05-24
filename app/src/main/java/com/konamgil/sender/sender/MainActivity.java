package com.konamgil.sender.sender;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_SMSSEND = 100;
    private Context mContext = MainActivity.this;
    private EditText etTimeCount;
    private EditText etTimeKinds;
    private EditText etRepeatCount;
    private EditText etMessage;
    private XmlHelper mXmlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //위젯 init
        init();

        //퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

    }

    /**
     * 위젯 초기화
     */
    private void init() {
        etTimeCount = (EditText) findViewById(R.id.etTimeCount);
        etTimeKinds = (EditText) findViewById(R.id.etTimeKinds);
        etRepeatCount = (EditText) findViewById(R.id.etRepeatCount);
        etMessage = (EditText) findViewById(R.id.etMessage);
        Button btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendSMS.setOnClickListener(mOnClickListener);
    }

    /**
     * xmlhelper 로 부터 받아온 string xml data
     * @return
     */
    private String resultXml(){

        String stTimeCount = etTimeCount.getText().toString();
        String stTimeKinds = etTimeKinds.getText().toString().trim();
        String stRepeatCount = etRepeatCount.getText().toString();
        String stMessage = etMessage.getText().toString();
        /**
         * xml 파싱하기
         */
        mXmlHelper = new XmlHelper(stTimeCount, stTimeKinds, stRepeatCount, stMessage, mContext);
        //
        String result = mXmlHelper.getXmlData();
        return result;
    }
    /**
     * SEND 버튼 리스너
     */
    private Button.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = "01072553466";

            if(isKind(etTimeKinds.getText().toString().trim())){
                String smsText = resultXml();
                SendSMS(phoneNumber, smsText );
            } else {
                Toast.makeText(mContext,"올바른 시간종류를 입력하세요",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * kinds 조사
     * @param str
     * @return
     */
    public boolean isKind(String str){
        boolean result;
        switch (str.toLowerCase()){
            case "s":
                result = true;
                break;
            case "m":
                result = true;
                break;
            case "h":
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * 숫자 유효성 체크
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        //먼저 유효성 체크
        if(str==null || str.equals("")) {
            Toast.makeText(mContext,"빈칸을 입력해주세요",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(int i=0; i<str.length(); i++) {
            char ch = str.charAt(i);

            if(ch<'0' || ch>'9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 장문 메시지는 이 메소드를 이용해야함
     * @param phonenumber
     * @param message
     */
    private void SendSMS(String phonenumber, String message) {
        registerReceiver();

        //sms 메니저 초기화 및 보내는 부분
        SmsManager smsManager = SmsManager.getDefault();
        String sendTo = phonenumber;

        // 메시지 내용
        ArrayList<String> partMessage = smsManager.divideMessage(message);

        //sent intent
        ArrayList<PendingIntent> arraySentIntent = new ArrayList<>();
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
        arraySentIntent.add(sentIntent);

        //delivered intent
        ArrayList<PendingIntent> arrayDeliveredIntent = new ArrayList<>();
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);
        arrayDeliveredIntent.add(deliveredIntent);

        smsManager.sendMultipartTextMessage(sendTo, null, partMessage,  arraySentIntent, arrayDeliveredIntent);

//        finish();

    }

    /**
     * 브로드 캐스트 리시버 : 문자 수신에 관해서 토스트
     */
    public void registerReceiver(){
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 전송 성공
                        Toast.makeText(mContext, "문자가 수신되었습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패
                        Toast.makeText(mContext, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아님
                        Toast.makeText(mContext, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 꺼짐
                        Toast.makeText(mContext, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패
                        Toast.makeText(mContext, "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 도착 완료
                        Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안됨
                        Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));
    }
    /**
     * 퍼미션 체크
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "SEND_SMS", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SMSSEND);


        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
        }
    }
}