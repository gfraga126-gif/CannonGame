import android.content.Context;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Paint paint;

    // Sons
    private SoundPool soundPool;
    private int soundCannonFire;
    private int soundTargetHit;
    private int soundBlockerHit;

    // Canhão
    private float cannonX;
    private float cannonY;
    private float aimX;
    private float aimY;

    // Tiro
    private float bulletX;
    private float bulletY;
    private float bulletSpeedX;
    private float bulletSpeedY;
    private boolean bulletActive = false;

    // Alvos
    private float targetX = 700;
    private float targetY = 300;
    private float targetSize = 80;

    private float blockerX = 500;
    private float blockerY = 450;
    private float blockerSize = 80;

    // Pontuação
    private int score = 0;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

        // Configuração dos sons
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        soundCannonFire = soundPool.load(context, R.raw.cannon_fire, 1);
        soundTargetHit = soundPool.load(context, R.raw.target_hit, 1);
        soundBlockerHit = soundPool.load(context, R.raw.blocker_hit, 1);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new GameThread(holder);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null) {
            gameThread.setRunning(false);

            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {

        if (getWidth() > 0 && getHeight() > 0) {

            cannonX = getWidth() / 2f;
            cannonY = getHeight() - 100;

            // Movimento do tiro
            if (bulletActive) {

                bulletX += bulletSpeedX;
                bulletY += bulletSpeedY;

                // Colisão com o alvo
                RectF targetRect = new RectF(
                        targetX,
                        targetY,
                        targetX + targetSize,
                        targetY + targetSize
                );

                if (targetRect.contains(bulletX, bulletY)) {

                    score++;

                    bulletActive = false;

                    soundPool.play(
                            soundTargetHit,
                            1.0f,
                            1.0f,
                            1,
                            0,
                            1.0f
                    );

                    // Novo local do alvo
                    targetX = 100 + (float) (Math.random() * (getWidth() - 200));
                    targetY = 100 + (float) (Math.random() * 300);
                }

                // Colisão com o obstáculo
                RectF blockerRect = new RectF(
                        blockerX,
                        blockerY,
                        blockerX + blockerSize,
                        blockerY + blockerSize
                );

                if (blockerRect.contains(bulletX, bulletY)) {

                    bulletActive = false;

                    soundPool.play(
                            soundBlockerHit,
                            1.0f,
                            1.0f,
                            1,
                            0,
                            1.0f
                    );
                }

                // Tiro saiu da tela
                if (bulletX < 0 ||
                        bulletX > getWidth() ||
                        bulletY < 0 ||
                        bulletY > getHeight()) {

                    bulletActive = false;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fundo
        canvas.drawColor(Color.rgb(20, 30, 45));

        // Texto da pontuação
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Pontos: " + score, 30, 60, paint);

        // Alvo
        paint.setColor(Color.RED);
        canvas.drawCircle(
                targetX + targetSize / 2,
                targetY + targetSize / 2,
                targetSize / 2,
                paint
        );

        // Centro do alvo
        paint.setColor(Color.WHITE);
        canvas.drawCircle(
                targetX + targetSize / 2,
                targetY + targetSize / 2,
                20,
                paint
        );

        // Obstáculo
        paint.setColor(Color.GRAY);

        canvas.drawRect(
                blockerX,
                blockerY,
                blockerX + blockerSize,
                blockerY + blockerSize,
                paint
        );

        // Canhão
        paint.setColor(Color.DKGRAY);

        canvas.drawRect(
                cannonX - 30,
                cannonY - 20,
                cannonX + 30,
                cannonY + 30,
                paint
        );

        // Tiro
        if (bulletActive) {

            paint.setColor(Color.YELLOW);

            canvas.drawCircle(
                    bulletX,
                    bulletY,
                    12,
                    paint
            );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            aimX = event.getX();
            aimY = event.getY();

            shoot();

            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            aimX = event.getX();
            aimY = event.getY();

            return true;
        }

        return true;
    }

    private void shoot() {

        if (bulletActive) {
            return;
        }

        bulletX = cannonX;
        bulletY = cannonY;

        float dx = aimX - cannonX;
        float dy = aimY - cannonY;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            distance = 1;
        }

        bulletSpeedX = (dx / distance) * 20;
        bulletSpeedY = (dy / distance) * 20;

        bulletActive = true;

        // Som do disparo
        soundPool.play(
                soundCannonFire,
                1.0f,
                1.0f,
                1,
                0,
                1.0f
        );
    }

    public void pauseGame() {
        if (gameThread != null) {
            gameThread.setRunning(false);

            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeGame() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(getHolder());
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    public void releaseSounds() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private class GameThread extends Thread {

        private SurfaceHolder surfaceHolder;
        private boolean running;

        public GameThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {

            while (running) {

                Canvas canvas = null;

                try {

                    canvas = surfaceHolder.lockCanvas();

                    if (canvas != null) {

                        synchronized (surfaceHolder) {

                            update();

                            onDraw(canvas);
                        }
                    }

                } finally {

                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}