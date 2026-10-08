package com.example.cannongame;

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
    // variaveis de espaço e tempo
    private long startTime;
    private long timeLeft;
    private boolean isGameOver = false;
    private final long totalGameTime = 60000;


    // movimento senoidal do alvo

    private float targetBaseY = 250; // Eixo Y central da onda
    private float targetAngle = 0f;  // Ângulo para a função seno
    private float targetSpeedX = 4f; // Velocidade horizontal do alvo

    // =========================
    // SONS
    // =========================

    private SoundPool soundPool;
    private int cannonFireSound;
    private int targetHitSound;
    private int blockerHitSound;

    // =========================
    // CANHÃO
    // =========================

    private float cannonX;
    private float cannonY;

    private float aimX;
    private float aimY;

    // =========================
    // TIRO
    // =========================

    private float bulletX;
    private float bulletY;
    private float bulletSpeedX;
    private float bulletSpeedY;

    private final float bulletRadius = 12;

    private boolean bulletActive = false;

    // =========================
    // ALVO
    // =========================

    private float targetX = 700;
    private float targetY = 250;
    private float targetRadius = 50;

    // =========================
    // BLOCKER / OBSTÁCULO
    // =========================

    private float blockerX = 100;
    private float blockerY = 500;

    private float blockerWidth = 180;
    private float blockerHeight = 45;

    private float blockerSpeed = 5;

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

        // Som do canhão
        cannonFireSound =
                soundPool.load(
                        context,
                        R.raw.cannon_fire,
                        1
                );

        // Som do alvo
        targetHitSound =
                soundPool.load(
                        context,
                        R.raw.target_hit,
                        1
                );

        // Som do blocker
        blockerHitSound =
                soundPool.load(
                        context,
                        R.raw.blocker_hit,
                        1
                );

        setFocusable(true);
    }


    // ==================================================
    // SURFACE
    // ==================================================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        startTime = System.currentTimeMillis();

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

        if (isGameOver) return; // Interrompe a lógica se o jogo acabou

        timeLeft = totalGameTime - (System.currentTimeMillis() - startTime);
        if (timeLeft <= 0) {
            timeLeft = 0;
            isGameOver = true;
            return;
        }

        if (getWidth() <= 0 || getHeight() <= 0) return;

        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        // Posição do canhão
        cannonX = getWidth() / 2f;
        cannonY = getHeight() - 100;

        // =========================
        // MOVIMENTAÇÃO SENOIDAL DO ALVO
        // =========================
                targetX += targetSpeedX;
                targetAngle += 0.08f;
        // O alvo sobe e desce 60 pixels em relação à sua base
                targetY = targetBaseY + (float) Math.sin(targetAngle) * 60f;

        // Rebate nas bordas laterais da tela
                if (targetX + targetRadius >= getWidth()) {
                    targetX = getWidth() - targetRadius;
                    targetSpeedX = -Math.abs(targetSpeedX);
                } else if (targetX - targetRadius <= 0) {
                    targetX = targetRadius;
                    targetSpeedX = Math.abs(targetSpeedX);
                }


        // =========================
        // MOVIMENTO DO BLOCKER
        // =========================

        if (bulletActive) {
            blockerX += blockerSpeed;

            // Bateu na direita
            if (blockerX + blockerWidth >= getWidth()) {
                blockerX = getWidth() - blockerWidth;
                blockerSpeed = -Math.abs(blockerSpeed);
            }

            // Bateu na esquerda
            if (blockerX <= 0) {
                blockerX = 0;
                blockerSpeed = Math.abs(blockerSpeed);
            }



            // =========================
            // MOVIMENTO DO TIRO
            // =========================

            if (bulletActive) {
                bulletX += bulletSpeedX;
                bulletY += bulletSpeedY;


                // =========================
                // COLISÃO COM BLOCKER
                // =========================


                RectF blockerRect =
                        new RectF(
                                blockerX,
                                blockerY,
                                blockerX + blockerWidth,
                                blockerY + blockerHeight
                        );

                RectF bulletRect =
                        new RectF(
                                bulletX - bulletRadius,
                                bulletY - bulletRadius,
                                bulletX + bulletRadius,
                                bulletY + bulletRadius
                        );


                if (RectF.intersects(
                        blockerRect,
                        bulletRect)) {

                    // Destrói o tiro
                    bulletActive = false;

                    // Som do blocker
                    if (soundPool != null) {

                        soundPool.play(
                                blockerHitSound,
                                1.0f,
                                1.0f,
                                1,
                                0,
                                1.0f
                        );
                    }

                    return;
                }
            }


            float dx = bulletX - targetX;
            float dy = bulletY - targetY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < targetRadius + bulletRadius) {

                // Desativa o tiro e aumenta a pontuação
                bulletActive = false;
                score++;

                // =========================
                // DIFICULDADE PROGRESSIVA (NOVO)
                // =========================
                // Aumenta a velocidade do blocker (mantendo a direção atual)
                blockerSpeed += (blockerSpeed > 0) ? 0.8f : -0.8f;

                // Diminui o tamanho do alvo até um limite mínimo de 20
                if (targetRadius > 20) {
                    targetRadius -= 2;
                }

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
                // NOVO LOCAL DO ALVO
                // =========================
                float margin = targetRadius + 20;
                float availableWidth = getWidth() - (margin * 2);

                if (availableWidth > 0) {
                    targetX = margin + (float) Math.random() * availableWidth;
                }

                targetBaseY = 120 + (float) Math.random() * 250;
                targetSpeedX += (targetSpeedX > 0) ? 0.5f : -0.5f; // Dificuldade: alvo mais rápido

                return;
            }


            // =========================
            // TIRO SAIU DA TELA
            // =========================

            if (bulletX < -bulletRadius ||
                    bulletX >
                            getWidth() + bulletRadius ||
                    bulletY < -bulletRadius ||
                    bulletY >
                            getHeight() + bulletRadius) {

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

        // Fundo existente...
        canvas.drawColor(Color.rgb(25, 35, 50));

        // HUD de Pontuação e Tempo
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);
        canvas.drawText("Pontos: " + score, 30, 60, paint);
        canvas.drawText("Tempo: " + (timeLeft / 1000) + "s", getWidth() - 300, 60, paint);

        if (isGameOver) {
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME OVER", getWidth() / 2f, getHeight() / 2f - 50, paint);

            paint.setTextSize(50);
            canvas.drawText("Pontuação final: " + score, getWidth() / 2f, getHeight() / 2f + 30, paint);

            paint.setTextSize(40);
            canvas.drawText("Toque para jogar novamente", getWidth() / 2f, getHeight() / 2f + 100, paint);

            paint.setTextAlign(Paint.Align.LEFT); // Reseta o alinhamento
            return; // Interrompe o desenho para ocultar os elementos do jogo
        }

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

        paint.setColor(Color.WHITE);

        canvas.drawCircle(
                targetX,
                targetY,
                15,
                paint
        );


        // =========================
        // BLOCKER
        // =========================

        paint.setColor(
                Color.rgb(
                        255,
                        140,
                        0
                )
        );

        canvas.drawRoundRect(
                blockerX,
                blockerY,
                blockerX + blockerWidth,
                blockerY + blockerHeight,
                15,
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
                    bulletRadius,
                    paint
            );
        }
    }


    // ==================================================
    // TOQUE
    // ==================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isGameOver) {
                // Restaura o estado inicial da partida
                targetSpeedX = 4f;
                targetBaseY = 250;
                score = 0;
                targetRadius = 50;
                blockerSpeed = 5;
                startTime = System.currentTimeMillis();
                isGameOver = false;
                return true;
            }

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

        // Apenas um tiro por vez
        if (bulletActive) {
            return;
        }


        bulletX = cannonX;

        bulletY = cannonY;


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


        // Velocidade do tiro
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
    // PAUSAR
    // ==================================================

    public void pause() {

        if (gameThread != null) {

            gameThread.setRunning(false);

            try {

                gameThread.join();

            } catch (InterruptedException e) {

                Thread.currentThread()
                        .interrupt();
            }
        }
    }


    // ==================================================
    // RETOMAR
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
    // GAME LOOP
    // ==================================================

    private class GameThread
            extends Thread {

        private final SurfaceHolder
                surfaceHolder;

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
                            surfaceHolder
                                    .lockCanvas();


                    if (canvas != null) {

                        synchronized (
                                surfaceHolder) {

                            update();

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

                } catch (
                        InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    return;
                }
            }
        }
    }
}