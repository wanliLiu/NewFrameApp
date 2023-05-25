/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soli.newframeapp.audio

import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat

/**
 * Manages a set of media metadata that is used to create a playlist for [VideoActivity].
 */
val mediaCatalog: List<MediaDescriptionCompat> = listOf(
    with(MediaDescriptionCompat.Builder()) {
        setDescription("MP4 loaded over HTTP")
        setMediaId("1")
        // License - https://peach.blender.org/download/
        setMediaUri(Uri.parse("http://180.184.103.46:53045/scene.mp4"))
        setTitle("Short film Big Buck Bunny")
        setSubtitle("Streaming video")
        build()
    },
    with(MediaDescriptionCompat.Builder()) {
        setDescription("MP4 loaded over HTTP")
        setMediaId("2")
        // License - https://archive.org/details/ElephantsDream
        setMediaUri(Uri.parse("http://180.184.103.46:53045/prologue.mp4"))
        setTitle("Short film Elephants Dream")
        setSubtitle("Streaming video")
        build()
    },
    with(MediaDescriptionCompat.Builder()) {
        setDescription("MOV loaded over HTTP")
        setMediaId("3")
        // License - https://mango.blender.org/sharing/
        setMediaUri(Uri.parse("http://180.184.103.46:53045/prologue.mp4"))
        setTitle("Short film Tears of Steel")
        setSubtitle("Streaming audio")
        build()
    }
)