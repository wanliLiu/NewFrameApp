package com.soli.newframeapp.palette

import android.annotation.SuppressLint
import android.content.Context
import androidx.palette.graphics.Palette
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.libcommon.util.FrescoUtil
import com.soli.libcommon.util.ImageLoader
import com.soli.newframeapp.databinding.ActivityPaletteBinding
import com.soli.newframeapp.databinding.ItemPaletteBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 *
 * @author Soli
 * @Time 2019/4/12 11:10
 */
class PaletteActivity : BaseActivity<ActivityPaletteBinding>() {

    override fun initView() {
        title = "Android Palette"
        binding.paletteRecyclerView.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(ctx, 2)
    }

    override fun initListener() {
    }

    override fun initData() {

        val list = ArrayList<String>()
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/7C/ChR47Fyu5fmAKqFGAAIQPkAG-jw40.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0207/M00/54/C3/ChR47Fyu5aeASpZbAAIW-BoAx8M52.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0206/M00/25/F6/ChR47FylsOKAYPBJAADlUYYEGLU97.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/63/ChR461ykE4OAQjgKAAOcpv0N8gs08.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/62/ChR461yiy8KADDgTAADshWPUGCk22.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/54/3E/ChR461yZiaqAerP_AAFZZIMo_Po51.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/51/52/ChR47FydwROAcjleAAMNruosgv479.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/58/ChR461ydfuiAO0apAANBJR8UhsY16.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0206/M00/25/ED/ChR47FycelaAbUZjABAg4BR4-aQ315.png")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/55/ChR47FycdmuAThMwAAtdi0_Ss5A481.png")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/54/42/ChR461yZ6j6AQFKEABHAgYqDpn8637.png")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/3C/ChR47FyQlx-AEaNSAAClXiK99ug33.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/49/ChR461yUTd6AMRv6AAr1etRvN-c094.png")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/51/42/ChR47FyUSveATrDiAAiCUpMyZ28793.png")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/51/42/ChR47FyUSQeAWThPAAxzDYvRYYc749.png")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/51/42/ChR47FyUSDGAfBYlAA5pCa34Swo534.png")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/51/42/ChR47FyUQi6Aer59AA07EcThBFQ231.png")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/48/ChR461yTo6OAS5KdAAFKADjAAg045.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0209/M00/59/47/ChR461yTY8SAdLzQAADzojuQHpI78.jpeg")
        list.add("https://dev-img01-joker.taihe.com/0208/M00/54/34/ChR461yTMLOAXyxrAAKl998iy1o395.png")

        binding.paletteRecyclerView.adapter = paletteAdapter(ctx, list)
    }


    private class paletteAdapter(ctx: Context, list: ArrayList<String>) :
        BaseRecycleAdapter<String, ItemPaletteBinding>(ctx, list) {

        @SuppressLint("CheckResult")
        override fun onBindView(
            binding: ItemPaletteBinding,
            itemType: Int,
            originalPosition: Int,
            realPosition: Int,
            payloads: List<Any>
        ) {
            val path = getItemData(realPosition) ?: return

            ImageLoader.loadImage(binding.palette, path)
            FrescoUtil.fetchBitmap(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Palette.from(it).generate { palette ->
                        palette?.apply {
                            var vibrant = palette.vibrantSwatch//palette.vibrantSwatch
                            if (vibrant == null)
                                for (temp in palette.swatches) {
                                    vibrant = temp
                                    break
                                }

                            binding.root.setBackgroundColor(vibrant!!.rgb)
                            binding.tv1.setTextColor(vibrant.titleTextColor)
                            binding.tv2.setTextColor(vibrant.bodyTextColor)
                        }

                    }
                }
        }
    }
}