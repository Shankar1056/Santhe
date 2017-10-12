package bigappcompany.com.santhe.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import bigappcompany.com.santhe.interfaces.SmsListener;

/**
 * Created by Pushpanjali on 9/6/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    Boolean b = false;
    String abcd, xyz;
    String messageBody = "";

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Pushpa", "data");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("Pushpa", "data");
            Bundle data = intent.getExtras();
            Log.d("Pushpa", "data" + data);
            Object[] pdus = (Object[]) data.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                messageBody = smsMessage.getMessageBody();

                Log.d("Pushpa", "data" + messageBody);

                //Pass on the text to our listener.
//                mListener.messageReceived(messageBody);
            }

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = smsMessage.getDisplayOriginatingAddress();
                b = sender.contains("Spotsoon");  //Just to fetch otp sent from WNRCRP
                String messageBody = smsMessage.getMessageBody();
                abcd = messageBody.replaceAll("[^0-9]", "");   // here abcd contains otp

                //Pass on the text to our listener.
//                if(b==true) {
                try {
                    mListener.messageReceived(abcd);

                } catch (Exception e) {

                    e.printStackTrace();
                }

                // attach value to interface
//                }
//                else
//                {
//                }
//            }
            }


        }

    }
}
