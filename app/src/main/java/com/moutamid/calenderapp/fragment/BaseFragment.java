package com.moutamid.calenderapp.fragment;

import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.moutamid.calenderapp.R;

public class BaseFragment extends Fragment {

    public void setStatusBarTranslucent() {
        Window window = getActivity().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    public void setStatusBarColor() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.grey));
    }

}
