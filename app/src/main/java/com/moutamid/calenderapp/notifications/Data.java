package com.moutamid.calenderapp.notifications;

import android.content.Context;

import com.moutamid.calenderapp.R;

import java.util.ArrayList;

public class Data {

    public static ArrayList<NotiModel> getData(Context context) {
        ArrayList<NotiModel> list = new ArrayList<>();

        list.add(new NotiModel( context.getResources().getString(R.string.stating_in_few_minutes),
                context.getResources().getString(R.string.the_event_is_stating_in_15_minutes_prepare_yourself_for_the_best_experience)
        ));
        list.add(new NotiModel(  context.getResources().getString(R.string.is_started),
                context.getResources().getString(R.string.did_you_miss_the_event_hurry_up_and_participate_the_event_and_don_t_forgot_to_capture_share_relive)
        ));
        return list;
    }


}
