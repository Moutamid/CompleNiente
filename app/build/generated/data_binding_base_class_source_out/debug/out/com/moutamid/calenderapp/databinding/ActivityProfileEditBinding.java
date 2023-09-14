// Generated by view binder compiler. Do not edit!
package com.moutamid.calenderapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.calenderapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityProfileEditBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextInputLayout bio;

  @NonNull
  public final TextInputLayout day;

  @NonNull
  public final LinearLayout dobLayout;

  @NonNull
  public final TextInputLayout email;

  @NonNull
  public final RelativeLayout image;

  @NonNull
  public final TextInputLayout month;

  @NonNull
  public final TextInputLayout name;

  @NonNull
  public final CircleImageView profileImage;

  @NonNull
  public final ToolbarBinding toolbar;

  @NonNull
  public final MaterialButton update;

  @NonNull
  public final TextInputLayout username;

  @NonNull
  public final TextInputLayout year;

  private ActivityProfileEditBinding(@NonNull RelativeLayout rootView, @NonNull TextInputLayout bio,
      @NonNull TextInputLayout day, @NonNull LinearLayout dobLayout, @NonNull TextInputLayout email,
      @NonNull RelativeLayout image, @NonNull TextInputLayout month, @NonNull TextInputLayout name,
      @NonNull CircleImageView profileImage, @NonNull ToolbarBinding toolbar,
      @NonNull MaterialButton update, @NonNull TextInputLayout username,
      @NonNull TextInputLayout year) {
    this.rootView = rootView;
    this.bio = bio;
    this.day = day;
    this.dobLayout = dobLayout;
    this.email = email;
    this.image = image;
    this.month = month;
    this.name = name;
    this.profileImage = profileImage;
    this.toolbar = toolbar;
    this.update = update;
    this.username = username;
    this.year = year;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityProfileEditBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityProfileEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_profile_edit, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityProfileEditBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bio;
      TextInputLayout bio = ViewBindings.findChildViewById(rootView, id);
      if (bio == null) {
        break missingId;
      }

      id = R.id.day;
      TextInputLayout day = ViewBindings.findChildViewById(rootView, id);
      if (day == null) {
        break missingId;
      }

      id = R.id.dobLayout;
      LinearLayout dobLayout = ViewBindings.findChildViewById(rootView, id);
      if (dobLayout == null) {
        break missingId;
      }

      id = R.id.email;
      TextInputLayout email = ViewBindings.findChildViewById(rootView, id);
      if (email == null) {
        break missingId;
      }

      id = R.id.image;
      RelativeLayout image = ViewBindings.findChildViewById(rootView, id);
      if (image == null) {
        break missingId;
      }

      id = R.id.month;
      TextInputLayout month = ViewBindings.findChildViewById(rootView, id);
      if (month == null) {
        break missingId;
      }

      id = R.id.name;
      TextInputLayout name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.profileImage;
      CircleImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.toolbar;
      View toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }
      ToolbarBinding binding_toolbar = ToolbarBinding.bind(toolbar);

      id = R.id.update;
      MaterialButton update = ViewBindings.findChildViewById(rootView, id);
      if (update == null) {
        break missingId;
      }

      id = R.id.username;
      TextInputLayout username = ViewBindings.findChildViewById(rootView, id);
      if (username == null) {
        break missingId;
      }

      id = R.id.year;
      TextInputLayout year = ViewBindings.findChildViewById(rootView, id);
      if (year == null) {
        break missingId;
      }

      return new ActivityProfileEditBinding((RelativeLayout) rootView, bio, day, dobLayout, email,
          image, month, name, profileImage, binding_toolbar, update, username, year);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
