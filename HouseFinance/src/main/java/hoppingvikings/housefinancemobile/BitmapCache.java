package hoppingvikings.housefinancemobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

/**
 * Created by iView on 04/07/2017.
 */

public class BitmapCache {
    private LruCache<String, Bitmap> _lruImageCache;

    private Context _context;

    public BitmapCache(long cachesize, Context context)
    {
        _context = context;
        _lruImageCache = new LruCache<String, Bitmap>((int)cachesize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void PutBitmap(String url, final String name, final ImageView imgview)
    {
        if(_lruImageCache.get(name) == null)
        {
            Glide.with(_context).load(url).asBitmap().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {

                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    _lruImageCache.put(name, bitmap);
                    imgview.setImageBitmap(_lruImageCache.get(name));
                }
            });
        }
        else
        {
            imgview.setImageBitmap(GetBitmap(name));
        }
        //_lruImageCache.put(name, image);
    }

    public Bitmap GetBitmap(String name)
    {
        if(_lruImageCache.get(name) != null)
        {
            return _lruImageCache.get(name);
        }
        else
        {
            return null;
        }

    }
}
