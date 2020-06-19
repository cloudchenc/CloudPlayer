package com.cloud.player.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.AbstractMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by cloud on 2020/4/17.
 */
public final class ExoMediaPlayer extends AbstractMediaPlayer implements Player.EventListener, VideoListener {

    private final static String TAG = ExoMediaPlayer.class.getSimpleName();

    private SimpleExoPlayer mSimpleExoPlayer;
    private Context mContext;
    private String mDataSource;
    private MediaSource mMediaSource;
    private DefaultBandwidthMeter mBandwidthMeter;

    public ExoMediaPlayer(Context context) {
        this.mContext = context;

        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        mSimpleExoPlayer = new SimpleExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector).build();

        mBandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();

        mSimpleExoPlayer.addListener(this);
        mSimpleExoPlayer.addVideoListener(this);
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            setSurface(null);
        } else {
            setSurface(surfaceHolder.getSurface());
        }
    }

    @Override
    public void setSurface(Surface surface) {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setVideoSurface(surface);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(context, uri, null);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> map) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mDataSource = uri.toString();
        String scheme = uri.getScheme();

        DefaultDataSource.Factory factory;
        if (map != null && map.size() > 0
                && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
            factory = new DefaultHttpDataSourceFactory(
                    "CloudPlayer",
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true
            );
            ((DefaultHttpDataSourceFactory) factory).getDefaultRequestProperties().set(map);
        } else {
            factory = new DefaultDataSourceFactory(context, "CloudPlayer", mBandwidthMeter);
        }

        mMediaSource = getMediaSource(uri, factory);
    }

    private MediaSource getMediaSource(Uri uri, DataSource.Factory factory) {
        int contentType = Util.inferContentType(uri);
        switch (contentType) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(factory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(factory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(factory).createMediaSource(uri);
            case C.TYPE_OTHER:
            default:
                return new ExtractorMediaSource.Factory(factory).createMediaSource(uri);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException, IllegalArgumentException, IllegalStateException {
        // do nothing
    }

    @Override
    public void setDataSource(String s) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(mContext, Uri.parse(s));
    }

    @Override
    public String getDataSource() {
        return mDataSource;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mSimpleExoPlayer.prepare(mMediaSource);
        mSimpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void start() throws IllegalStateException {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void setScreenOnWhilePlaying(boolean b) {

    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mSimpleExoPlayer != null && mSimpleExoPlayer.getPlayWhenReady();
    }

    @Override
    public void seekTo(long l) throws IllegalStateException {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.seekTo(l);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mSimpleExoPlayer == null) {
            return 0;

        }
        return mSimpleExoPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        if (mSimpleExoPlayer == null) {
            return 0;
        }
        return mSimpleExoPlayer.getDuration();
    }

    @Override
    public void release() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.release();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void setVolume(float v, float v1) {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.setVolume((v + v1) / 2);
        }
    }

    public void setSpeed(float speed) {
        if (mSimpleExoPlayer != null) {
            PlaybackParameters parameters = new PlaybackParameters(speed, 1.0f);
            mSimpleExoPlayer.setPlaybackParameters(parameters);
        }
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public void setLogEnabled(boolean b) {

    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public void setAudioStreamType(int i) {

    }

    @Override
    public void setKeepInBackground(boolean b) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context context, int i) {

    }

    @Override
    public void setLooping(boolean b) {

    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return new ITrackInfo[0];
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_IDLE:
            case Player.STATE_READY:
                notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_END, mSimpleExoPlayer.getBufferedPercentage());
                break;
            case Player.STATE_BUFFERING:
                notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_START, mSimpleExoPlayer.getBufferedPercentage());
                break;
            case Player.STATE_ENDED:
                notifyOnCompletion();
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        notifyOnError(error.type, error.type);
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        notifyOnVideoSizeChanged(width, height, 1, 1);
    }
}
