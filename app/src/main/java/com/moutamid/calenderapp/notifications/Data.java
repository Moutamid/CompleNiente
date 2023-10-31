package com.moutamid.calenderapp.notifications;

import java.util.ArrayList;

public class Data {

    public static ArrayList<NotiModel> getData() {
        ArrayList<NotiModel> list = new ArrayList<>();

        list.add(new NotiModel( " Stating in few Minutes",
                "The event is stating in 15 minutes prepare yourself for the best experience."
        ));
        list.add(new NotiModel(  " is started",
                "Did you miss the event 😕. Hurry up and participate the event and don't forgot to Capture, Share & Relive!"
        ));
//        list.add(new NotiModel( "Whatsapp Web",
//                "WhatsApp on the web! Stay connected seamlessly with WhatsApp Web. Chat, share, and sync across devices.💻📱"
//        ));
        return list;
    }


}
