/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.toro.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * @author eneim (2018/01/24).
 * @since 3.4.0
 */
public interface MediaSourceBuilder {

  @NonNull MediaSource buildMediaSource(Context context, Uri uri, Handler handler,
      DataSource.Factory manifestDataSourceFactory, DataSource.Factory mediaDataSourceFactory,
      @SuppressWarnings("SameParameterValue") MediaSourceEventListener listener);

  MediaSourceBuilder DEFAULT = new MediaSourceBuilder() {
    @NonNull @Override
    public MediaSource buildMediaSource(Context context, Uri uri, Handler handler,
        DataSource.Factory manifestDataSourceFactory, DataSource.Factory mediaDataSourceFactory,
        MediaSourceEventListener listener) {
      @C.ContentType int type = Util.inferContentType(uri);
      switch (type) {
        case C.TYPE_DASH:
          return new DashMediaSource.Factory(
              new DefaultDashChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
              .createMediaSource(uri, handler, listener);
        case C.TYPE_HLS:
          return new HlsMediaSource.Factory(mediaDataSourceFactory) //
              .createMediaSource(uri, handler, listener);
        case C.TYPE_OTHER:
          return new ExtractorMediaSource.Factory(mediaDataSourceFactory) //
              .createMediaSource(uri, handler, listener);
        case C.TYPE_SS:
          return new SsMediaSource.Factory( //
              new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)//
              .createMediaSource(uri, handler, listener);
        default:
          throw new IllegalStateException("Unsupported type: " + type);
      }
    }
  };

  MediaSourceBuilder LOOPING = new MediaSourceBuilder() {
    @NonNull @Override
    public MediaSource buildMediaSource(Context context, Uri uri, Handler handler,
        DataSource.Factory manifestDataSourceFactory, DataSource.Factory mediaDataSourceFactory,
        MediaSourceEventListener listener) {
      return new LoopingMediaSource(
          DEFAULT.buildMediaSource(context, uri, handler, manifestDataSourceFactory,
              mediaDataSourceFactory, listener));
    }
  };
}
