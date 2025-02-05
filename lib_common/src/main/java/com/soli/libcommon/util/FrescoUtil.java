package com.soli.libcommon.util;

import static com.facebook.drawee.backends.pipeline.Fresco.getImagePipeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.logging.FLog;
import com.facebook.common.logging.FLogDefaultLoggingDelegate;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.soli.libcommon.net.ApiHelper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by Soli on 2016/8/17.
 */
public class FrescoUtil {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

    private static final String IMAGE_PIPELINE_CACHE_DIR = "fresco_image_cache";

    private static ImagePipelineConfig sImagePipelineConfig;

    /**
     * @param ctx
     */
    public static void Init(Context ctx) {
        try {
            Fresco.initialize(ctx, getImagePipelineConfig(ctx));
            FLogDefaultLoggingDelegate.getInstance().setApplicationTag("FrescoLog");
            FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates config using android http stack as network backend.
     */
    private static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
//            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            //网络层用 okhttp 做了相应的https兼容
            ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(context, ApiHelper.getClient());
            configureCaches(configBuilder, context);
            configureLoggingListeners(configBuilder);
            configureOptions(configBuilder);
            sImagePipelineConfig = configBuilder.build();

            NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(trimType -> {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackgroundLowSeverity.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                ) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            });
        }
        return sImagePipelineConfig;
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder.setBitmapMemoryCacheParamsSupplier(
                () -> bitmapCacheParams)
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(context)
                                .setBaseDirectoryPath(context.getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .build());
    }

    /**
     * @param configBuilder
     */
    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        configBuilder.setRequestListeners(requestListeners);
    }

    /**
     * @param configBuilder
     */
    private static void configureOptions(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setDownsampleEnabled(true);
        configBuilder.setCacheKeyFactory(new MyCacheKeyFactory());
    }

    /**
     * 查找一个bitmap是否被缓存
     * 存在memory 或 diskcache，就算有
     *
     * @param url
     */
    public static boolean isUrlExistInCache(String url) {
        if (!TextUtils.isEmpty(url)) {
            boolean inMemoryCache = getImagePipeline().isInBitmapMemoryCache(Uri.parse(url));
            if (inMemoryCache) {//如果内存中有，就返回
                return true;
            } else {//内存中没有，查询磁盘缓存是否有
                File file = getFrescoCacheFile(url);
                return (file != null && file.exists()) ? true : false;
            }
        }
        return false;
    }

    /**
     * 获取fresco 缓存的文件
     *
     * @param loadUri
     * @return
     */
    public static File getFrescoCacheFile(String loadUri) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(loadUri));
        imageRequestBuilder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE);
        CacheKey cacheKey = MyCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequestBuilder.build(), null);
        BinaryResource resource = ImagePipelineFactory.getInstance().getDiskCachesStoreSupplier().get().getMainFileCache().getResource(cacheKey);
        if (resource != null) {
            return ((FileBinaryResource) resource).getFile();
        }
        return null;
    }

    /**
     * @param url
     * @return
     */
    public static Observable<Bitmap> fetchBitmap(final String url) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<Bitmap> subscriber) throws Exception {
                ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
                builder.setImageDecodeOptions(new ImageDecodeOptionsBuilder()
                        .setMinDecodeIntervalMs(100)
                        .setForceStaticImage(true)
                        .build());
                DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(builder.build(), null);
                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            subscriber.onNext(bitmap);
                            subscriber.onComplete();
                        } else {
                            subscriber.onError(new Throwable("获取bitmap为空"));
                        }
                    }

                    @Override
                    public void onCancellation(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        super.onCancellation(dataSource);
                        subscriber.onError(new Throwable("取消获取bitmap"));
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
                        subscriber.onError(dataSource.getFailureCause());
                    }
                }, UiThreadImmediateExecutorService.getInstance());
            }
        }).subscribeOn(Schedulers.io());
    }
}
