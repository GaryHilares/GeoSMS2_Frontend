package localhost.garyhilares.geosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyBroadCastReceiver extends BroadcastReceiver {
    Logger logger;
    MyBroadCastReceiver(Logger newLogger){
        logger = newLogger;
    }

    @Override
    public void onReceive(Context ctx, Intent intent){
        /* Check version */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            /* Iterate over new messages */
            for(SmsMessage message: Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                String author = message.getOriginatingAddress();
                String messageContent = message.getDisplayMessageBody();
                logger.addMessage(String.format("Message \"%s\" from %s", messageContent, author));
                /* Create request */
                StringRequest str_req = new StringRequest(Request.Method.POST,"https://geosms-2.vercel.app/command",
                        /* On response */
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response){
                                logger.addMessage(String.format("Response to StringRequest: \"%s\"", messageContent));
                                SmsManager manager = SmsManager.getDefault();
                                ArrayList<String> parts = manager.divideMessage(response);
                                manager.sendMultipartTextMessage(message.getOriginatingAddress(),null, parts,null, null);
                                logger.addMessage(String.format("Successfully responded to multipart text message. Parts sent: %d", parts.size()));
                            }},
                        /* On error */
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError){
                                try {
                                    String responseBody = new String(volleyError.networkResponse.data, "utf-8").trim();
                                    logger.addMessage(String.format("StringRequest error received when replying to message \"%s\": %s", messageContent, responseBody));
                                } catch (UnsupportedEncodingException encodingError) {
                                    logger.addMessage(String.format("StringRequest error received when replying to message \"%s\". Error message could not be retrieved due to an UnsupportedEncodingException.", messageContent));
                                }
                            }
                }) {
                    /* Get request params */
                    @Override
                    public Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("sms", messageContent);
                        params.put("num", author);
                        return params;
                    }
                };

                /* Send request */
                RequestQueue queue = Volley.newRequestQueue(ctx);
                queue.add(str_req);
            }
        }
    }
}