package id.co.medical.management.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import id.co.medical.management.R;

public class LoadImageUtil {

    Context context;
    ProgressBar progressBar;

    public LoadImageUtil(Context context) {
        this.context = context;
    }

    public void setGlideWithAccent(String a, ImageView b) {
        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)
                .placeholder(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .centerCrop()
                .fitCenter();
        Glide.with(context)
                .load(a)
                .apply(requestOptions)
                .into(b);
    }

    public void setGlideWithLoading(String a, ImageView b) {
        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)
                .centerCrop()
                .fitCenter();
        Glide.with(context)
                .load(a)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(requestOptions)
                .into(b);
    }
}
