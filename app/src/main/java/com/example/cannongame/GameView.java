package com.example.cannongame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Paint paint;

    // =========================
    // SONS
    // =========================

    private SoundPool soundPool;
    private int cannonFireSound;
    private int targetHitSound;

    // =========================
    // CANHÃO
    // =========================

    private float cannonX;
    private float cannonY;

    // Mira
    private float aimX;
    private float aimY;

    // =========================
    // TIRO
    // =========================

    private float bulletX;
    private float bulletY;
    private float bulletSpeedX;
    private float bulletSpeedY;

    private boolean bulletActive = false;

    // =========================
    // ALVO
    // =========================

    private float targetX = 700;
    private float targetY = 250;
    private float targetRadius = 50;

    // =========================
    // PONTUAÇÃO
    // =========================

    private int score = 0;


    // ==================================================
    // CONSTRUTOR
    // ==================================================

    public GameView(Context context) {

        super(context);

        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

        // =========================
        // CONFIGURAÇÃO DOS SONS
        // =========================

        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION
                        )
                        .build();

        soundPool =
                new SoundPool.Builder()
                        .setMaxStreams(3)
                        .setAudioAttributes(audioAttributes)
                        .build();

        // Som do disparo
        cannonFireSound =
                soundPool.load(
                        context,
                        R.raw.cannon_fire,
                        1
                );

        // Som quando acertar o alvo
        targetHitSound =
                soundPool.load(
                        context,
                        R.raw.target_hit,
                        1
                );

        setFocusable(true);
    }


    // ==================================================
    // SURFACE
    // ==================================================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        gameThread = new GameThread(holder);

        gameThread.setRunning(true);

        gameThread.start();
    }


    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (gameThread != null) {

            gameThread.setRunning(false);

            try {

                gameThread.join();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
    }


    // ==================================================
    // ATUALIZAÇÃO DO JOGO
    // ==================================================

    private void update() {

        cannonX = getWidth() / 2f;

        cannonY = getHeight() - 100;


        if (bulletActive) {

            // Move o tiro
            bulletX += bulletSpeedX;

            bulletY += bulletSpeedY;


            // =========================
            // COLISÃO COM O ALVO
            // =========================

            float dx =
                    bulletX - targetX;

            float dy =
                    bulletY - targetY;

            float distance =
                    (float) Math.sqrt(
                            dx * dx + dy * dy
                    );


            if (distance < targetRadius) {

                bulletActive = false;

                // Aumenta pontuação
                score++;


                // =========================
                // SOM DO ACERTO
                // =========================

                if (soundPool != null) {

                    soundPool.play(
                            targetHitSound,
                            1.0f,
                            1.0f,
                            1,
                            0,
                            1.0f
                    );
                }


                // =========================
                // NOVA POSIÇÃO DO ALVO
                // =========================

                targetX =
                        100 +
                                (float) Math.random()
                                        * (getWidth() - 200);

                targetY =
                        100 +
                                (float) Math.random()
                                        * 300;
            }


            // =========================
            // TIRO SAIU DA TELA
            // =========================

            if (bulletX < 0 ||
                    bulletX > getWidth() ||
                    bulletY < 0 ||
                    bulletY > getHeight()) {

                bulletActive = false;
            }
        }
    }


    // ==================================================
    // DESENHO DO JOGO
    // ==================================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        // =========================
        // FUNDO
        // =========================

        canvas.drawColor(
                Color.rgb(
                        25,
                        35,
                        50
                )
        );


        // =========================
        // PONTUAÇÃO
        // =========================

        paint.setColor(Color.WHITE);

        paint.setTextSize(45);

        canvas.drawText(
                "Pontos: " + score,
                30,
                60,
                paint
        );


        // =========================
        // ALVO
        // =========================

        paint.setColor(Color.RED);

        canvas.drawCircle(
                targetX,
                targetY,
                targetRadius,
                paint
        );


        // Centro branco
        paint.setColor(Color.WHITE);

        canvas.drawCircle(
                targetX,
                targetY,
                15,
                paint
        );


        // =========================
        // CANHÃO
        // =========================

        paint.setColor(Color.DKGRAY);

        canvas.drawRect(
                cannonX - 35,
                cannonY - 20,
                cannonX + 35,
                cannonY + 30,
                paint
        );


        // =========================
        // TIRO
        // =========================

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


    // ==================================================
    // TOQUE NA TELA
    // ==================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction()
                == MotionEvent.ACTION_DOWN) {

            aimX = event.getX();

            aimY = event.getY();

            shoot();

            return true;
        }

        return true;
    }


    // ==================================================
    // DISPARO
    // ==================================================

    private void shoot() {

        // Só permite um tiro por vez
        if (bulletActive) {

            return;
        }


        // Tiro começa no canhão
        bulletX = cannonX;

        bulletY = cannonY;


        // Direção do toque
        float dx =
                aimX - cannonX;

        float dy =
                aimY - cannonY;


        float distance =
                (float) Math.sqrt(
                        dx * dx + dy * dy
                );


        if (distance == 0) {

            distance = 1;
        }


        // Velocidade
        bulletSpeedX =
                (dx / distance) * 20;

        bulletSpeedY =
                (dy / distance) * 20;


        bulletActive = true;


        // =========================
        // SOM DO DISPARO
        // =========================

        if (soundPool != null) {

            soundPool.play(
                    cannonFireSound,
                    1.0f,
                    1.0f,
                    1,
                    0,
                    1.0f
            );
        }
    }


    // ==================================================
    // PAUSAR JOGO
    // ==================================================

    public void pause() {

        if (gameThread != null) {

            gameThread.setRunning(false);

            try {

                gameThread.join();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
    }


    // ==================================================
    // RETOMAR JOGO
    // ==================================================

    public void resume() {

        if (gameThread == null ||
                !gameThread.isAlive()) {

            gameThread =
                    new GameThread(
                            getHolder()
                    );

            gameThread.setRunning(true);

            gameThread.start();
        }
    }


    // ==================================================
    // THREAD / GAME LOOP
    // ==================================================

    private class GameThread extends Thread {

        private final SurfaceHolder surfaceHolder;

        private boolean running;


        public GameThread(
                SurfaceHolder surfaceHolder) {

            this.surfaceHolder =
                    surfaceHolder;
        }


        public void setRunning(
                boolean running) {

            this.running =
                    running;
        }


        @Override
        public void run() {

            while (running) {

                Canvas canvas = null;

                try {

                    canvas =
                            surfaceHolder.lockCanvas();


                    if (canvas != null) {

                        synchronized (surfaceHolder) {

                            // Atualiza o jogo
                            update();

                            // Desenha o jogo
                            onDraw(canvas);
                        }
                    }

                } finally {

                    if (canvas != null) {

                        surfaceHolder
                                .unlockCanvasAndPost(
                                        canvas
                                );
                    }
                }


                // Aproximadamente 60 FPS
                try {

                    Thread.sleep(16);

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    return;
                }
            }
        }
    }
}