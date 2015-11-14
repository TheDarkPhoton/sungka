package helpers.frontend;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.deathgull.sungka_project.R;

public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder binder = new ServiceBinder();
    MediaPlayer player;
    private int length = 0;
    private boolean muted;

    public MusicService() {}

    /**
     * Gets the service so activites can bind to it (access methods)
     */
    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.background);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnErrorListener(this);

        if(player != null) {
            player.setLooping(true);
            player.setVolume(0.4f, 0.4f);
        }

        player.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onError(player, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(player != null) {
            player.start();
            muted = false;
        }
        return START_NOT_STICKY;
    }

    /**
     * mutes the music
     */
    public void muteMusic() {
        if(player.isPlaying() && !muted) {
            muted = true;
            player.setVolume(0f, 0f);
        }
    }

    /**
     * unmutes the music
     */
    public void unmuteMusic() {
        if(player.isPlaying() && muted) {
            muted = false;
            player.setVolume(0.4f, 0.4f);
        }
    }

    /**
     * CHekc sif muted
     * @return - mute state
     */
    public boolean isMuted() { return muted; }

    /**
     * pauses music
     */
    public void pauseMusic() {
        if(player.isPlaying()) {
            player.pause();
            length = player.getCurrentPosition();
        }
    }

    /**
     * resumes music
     */
    public void resumeMusic() {
        if(player != null && !player.isPlaying()) {
            player.seekTo(length);
            player.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player != null) {
            try {
                player.stop();
                player.release();
            } finally {
                player = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } finally {
                player = null;
            }
        }
        return false;
    }
}
