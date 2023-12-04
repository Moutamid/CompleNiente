package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.MediaPagerAdapter;
import com.moutamid.calenderapp.models.ChatsModel;

import java.util.ArrayList;

public class FullPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_preview);

        int position = getIntent().getIntExtra("position", 0);

        RecyclerView pager = findViewById(R.id.rc);
        MaterialCardView back = findViewById(R.id.back);

        pager.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pager.setHasFixedSize(false);

        back.setOnClickListener(v -> onBackPressed());

        ArrayList<ChatsModel> list = Stash.getArrayList("GALERYLIST", ChatsModel.class);

        MediaPagerAdapter adapter = new MediaPagerAdapter(this, list);
        pager.setAdapter(adapter);

        pager.scrollToPosition(position);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Stash.clear("GALERYLIST");
    }
}