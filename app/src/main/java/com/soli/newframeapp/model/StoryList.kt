package com.soli.newframeapp.model

/*
 * @author soli
 * @Time 2018/5/26 15:39
 */
data class StoryList(
        val date : String,
        val stories : List<Story>
){
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}