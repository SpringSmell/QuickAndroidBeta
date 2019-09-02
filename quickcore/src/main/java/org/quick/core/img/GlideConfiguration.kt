package org.quick.core.img

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * Created by work on 2017/3/20.
 */

@Suppress("JAVA_CLASS_ON_COMPANION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@GlideModule
class GlideConfiguration : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context,
                GLIDE_CARCH_DIR,
                GLIDE_CATCH_SIZE.toLong()))
        super.applyOptions(context, builder)
    }

    override fun isManifestParsingEnabled(): Boolean = false

    companion object {

        // 图片缓存最大容量，150M，根据自己的需求进行修改
        const val GLIDE_CATCH_SIZE = 1024 * 1024 * 1024

        // 图片缓存子目录
        val GLIDE_CARCH_DIR = javaClass.`package`.name + ":CacheQuick"
    }
}
