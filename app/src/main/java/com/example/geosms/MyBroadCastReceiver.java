package com.example.geosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            for(SmsMessage message: Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                RequestQueue queue = Volley.newRequestQueue(ctx);
                String base_url = "https://geosms-2.vercel.app/command";
                StringRequest str_req = new StringRequest(Request.Method.POST,base_url,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        SmsManager manager = SmsManager.getDefault();
                        ArrayList<String> parts = manager.divideMessage(response);
                        Toast.makeText(ctx,Integer.toString(parts.size()),Toast.LENGTH_LONG).show();
                        manager.sendMultipartTextMessage(message.getOriginatingAddress(),null, parts,null, null);
                    }},
                        new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(message.getOriginatingAddress(),null, "Something went wrong!",null, null);
                    }
                }) {
                    @Override
                    public Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("sms",message.getDisplayMessageBody());
                        return params;
                    }
                };
                queue.add(str_req);
            }
        }
    }
}