package im.chatify.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gpit.android.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.todddavies.components.progressbar.ProgressWheel;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import im.chatify.model.CIConst;

/**
 * Created by administrator on 7/8/15.
 */
public class CIUtils {

    private static final String QR_CODE_COLOR = "#000000";

    private static CIUtils instance;

    public static CIUtils getInstance(Context context) {
        if (instance == null) {
            instance = new CIUtils(context);
        }

        instance.setContext(context);

        return instance;
    }

    private Context mContext;
    private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();


    private CIUtils(Context context) {
        mContext = context;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    /*
    public void createQRCode(final String data, final int width,
                             final int height, final OnQRCodeCreateListener listener) {
        new AsyncTask<Void, Void, Bitmap>() {
            protected void onPreExecute() {
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap qrCodeBitmap = null;

                Writer writer = new QRCodeWriter();
                String codeData = Uri.encode(data, "utf-8");

                try {
                    Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(
                            EncodeHintType.class);
                    hints.put(EncodeHintType.MARGIN, 0);
                    BitMatrix bm = writer.encode(codeData,
                            BarcodeFormat.QR_CODE, width, width, hints);
                    qrCodeBitmap = Bitmap.createBitmap(width, width,
                            Config.ARGB_8888);

                    int[] pixels = new int[width * height];
                    for (int y = 0; y < height; y++) {
                        int offset = y * width;
                        for (int x = 0; x < width; x++) {
                            pixels[offset + x] = bm.get(x, y) ? Color.parseColor(QR_CODE_COLOR)
                                    : Color.TRANSPARENT;
                        }
                    }

                    qrCodeBitmap.setPixels(pixels, 0, width, 0, 0, width,
                            height);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                return qrCodeBitmap;
            }

            protected void onPostExecute(Bitmap result) {
                if (listener != null) {
                    listener.onCreatedBitmap(result);
                }
            }
        }.execute();
    }
    */

    public void loadImage(ImageView imageView, String url) {
        loadImage(imageView, url, null, null);
    }

    public void loadImage(ImageView imageView, String url,
                          DisplayImageOptions options, final ProgressWheel wheel) {

        if (wheel != null)
            wheel.setVisibility(View.GONE);

        if (options != null)
            imageView.setImageDrawable(options.getImageForEmptyUri(mContext.getResources()));

        imageView.setTag(url);
        ImageLoader imageLoader = CIUtils.getImageLoader(mContext);
        imageLoader.displayImage(url, imageView, options, mAnimateFirstListener, new ImageLoadingProgressListener() {

            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

                int progress = (int) ((float) (360.0f / total) * (float) current);

                if (wheel != null) {
                    wheel.setProgress(progress);

                    if (progress > 340.0f)
                        wheel.setVisibility(View.GONE);
                    else
                        wheel.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static ImageLoader getImageLoader(Context context) {

        ImageLoader imageLoader = ImageLoader.getInstance();

        if ( imageLoader.isInited() )
            return imageLoader;

        // Config image downloader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        return imageLoader;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        public AnimateFirstDisplayListener() {
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            if (view == null)
                return;

            /*
            ImageView imageView = (ImageView) view;

            String newUrl = (String) view.getTag();

            if (Utils.isNullOrEmpty(newUrl)) {
                imageView.setImageBitmap(defaultBitmap);
            } else if (newUrl == imageUri || newUrl.equals(imageUri)) {
                if (loadedImage == null) {
                    imageView.setImageBitmap(defaultBitmap);
                } else {
                    boolean firstDisplay = !displayedImages.contains(imageUri);
                    if (firstDisplay) {
                        FadeInBitmapDisplayer.animate(imageView, 500);
                        displayedImages.add(imageUri);
                    }
                    imageView.setImageBitmap(loadedImage);
                }
            }
            */
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }

    }

    public static Bitmap resizeImage(Bitmap bitmap, int width) {

        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();

        final int destWidth = width;//or the width you need

        if ( origWidth > destWidth ) {
            // picture is wider than we want it, we calculate its target height
            float ratio = (float) origWidth / destWidth;

            int destHeight = (int) ((float) origHeight/ ratio);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            resizeBitmap.compress(Bitmap.CompressFormat.JPEG, CIConst.IMAGE_COMPRESS_RATIO, outStream);
            // we save the file, at least until we have made use of it

            return resizeBitmap;
        }

        return bitmap;
    }

    public static float getPictureRatioByWidth(int width, int height) {

        float ratio = (float) height / width;

        return ratio;
    }

    public static String getXMPPResourceName(Context context) {

        String resourceName = "Android" + "-" + Utils.getOSVersion() + "-" + Utils.getDeviceName(context) + "-" + Utils.getAppVersionCode(context);

        return resourceName;
    }

    public static void imageViewAnimatedChange(Context context, final ImageView imageView, final Bitmap newImage, long duration) {

        final Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animOut.setDuration(duration);
        final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animIn.setDuration(duration);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                imageView.setImageBitmap(newImage);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animOut);
    }
}
