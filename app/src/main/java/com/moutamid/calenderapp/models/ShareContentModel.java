package com.moutamid.calenderapp.models;

import android.net.Uri;

public class ShareContentModel {
    Uri uri;
    String type;

    public ShareContentModel(Uri uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
