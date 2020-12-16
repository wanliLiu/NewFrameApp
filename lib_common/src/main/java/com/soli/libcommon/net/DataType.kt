package com.soli.libcommon.net

import androidx.annotation.IntDef

/*
 * @author soli
 * @Time 2018/5/19 16:15
 */
object DataType {
    /*返回数据为String*/
    const val STRING = 1

    /*返回数据为json对象*/
    const val JSON_OBJECT = 2

    /*返回数据为json数组*/
    const val JSON_ARRAY = 3

    /**
     * 自定义一个播放器状态注解
     */
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        STRING,
        JSON_OBJECT,
        JSON_ARRAY
    )
    annotation class Type
}