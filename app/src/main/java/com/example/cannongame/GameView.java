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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Paint paint;

    // =========================
    // VARIÁVEIS DE ESPAÇO E TEMPO
    // =========================
    private long startTime;
    private long timeLeft;
    private boolean isGameOver = false;
    private final long totalGameTime = 60000;

    // =========================
    // MOVIMENTO SENOIDAL DO ALVO
    // =========================
    private float targetBaseY = 250;
    private float targetAngle = 0f;
    private float targetSpeedX = 4f;

    // =========================
    // SONS
    // =========================
    private SoundPool soundPool;
    private int cannonFireSound;
    private int targetHitSound;
    private int blockerHitSound;

    // =========================
    // CANHÃO E MIRA (SWIPE)
    // =========================
    private float cannonX;
    private float cannonY;
    private float aimX;
    private float aimY;
    private boolean isAiming = false;

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

    private final float targetRadius = 50;

    // =========================
    // BLOCKER
    // =========================
    private float blockerX = 100;
    private float blockerY = 500;
<<<<<<< Updated upstream
    private float blockerWidth = 180;
    private float blockerHeight = 45;
=======

    private final float blockerWidth = 180;
    private final float blockerHeight = 45;

>>>>>>> Stashed changes
    private float blockerSpeed = 5;

    // =========================
    // PONTUAÇÃO E PARTÍCULAS
    // =========================
    private int score = 0;
<<<<<<< Updated upstream
    private List<Particle> particles = new ArrayList<>();
=======

    // =========================
    // TEMPO / GAME OVER
    // =========================

    private static final long GAME_DURATION = 60000;

    private long gameStartTime;

    private long timeRemaining = GAME_DURATION;

    private boolean gameOver = false;

>>>>>>> Stashed changes

    // ==================================================
    // CONSTRUTOR
    // ==================================================
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

<<<<<<< Updated upstream
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        cannonFireSound = soundPool.load(context, R.raw.cannon_fire, 1);
        targetHitSound = soundPool.load(context, R.raw.target_hit, 1);
        blockerHitSound = soundPool.load(context, R.raw.blocker_hit, 1);
=======
        // Configuração do áudio
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

        cannonFireSound =
                soundPool.load(
                        context,
                        R.raw.cannon_fire,
                        1
                );

        targetHitSound =
                soundPool.load(
                        context,
                        R.raw.target_hit,
                        1
                );

        blockerHitSound =
                soundPool.load(
                        context,
                        R.raw.blocker_hit,
                        1
                );
>>>>>>> Stashed changes

        setFocusable(true);
    }

    // ==================================================
    // SURFACE
    // ==================================================
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
<<<<<<< Updated upstream
        startTime = System.currentTimeMillis();
        gameThread = new GameThread(holder);
=======

        // Começa uma nova partida
        startNewGame();

        gameThread =
                new GameThread(holder);

>>>>>>> Stashed changes
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
<<<<<<< Updated upstream
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
=======
    public void surfaceDestroyed(
            SurfaceHolder holder) {

        stopGameThread();
>>>>>>> Stashed changes
    }

    // ==================================================
    // NOVA PARTIDA
    // ==================================================

    private void startNewGame() {

        score = 0;

        bulletActive = false;

        gameOver = false;

        // Marca quando a partida começou
        gameStartTime =
                System.currentTimeMillis();

        timeRemaining =
                GAME_DURATION;

        // Reinicia blocker
        blockerX = 100;

        blockerSpeed = 5;

        // Reposiciona o alvo
        if (getWidth() > 0) {

            targetX =
                    getWidth() / 2f;

        } else {

            targetX = 700;
        }

        targetY = 250;
    }


    // ==================================================
    // UPDATE
    // ==================================================
    private void update() {
        if (isGameOver) return;

<<<<<<< Updated upstream
        timeLeft = totalGameTime - (System.currentTimeMillis() - startTime);
        if (timeLeft <= 0) {
            timeLeft = 0;
            isGameOver = true;
            return;
        }

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // Posição do canhão
        cannonX = getWidth() / 2f;
        cannonY = getHeight() - 100;
=======
        if (getWidth() <= 0 ||
                getHeight() <= 0) {

            return;
        }


        cannonX =
                getWidth() / 2f;

        cannonY =
                getHeight() - 100;
>>>>>>> Stashed changes

        // =========================
        // ATUALIZAÇÃO DAS PARTÍCULAS
        // =========================
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (p.life <= 0) {
                iterator.remove();
            }
        }

        // =========================
        // MOVIMENTAÇÃO SENOIDAL DO ALVO
        // =========================
        targetX += targetSpeedX;
        targetAngle += 0.08f;
        targetY = targetBaseY + (float) Math.sin(targetAngle) * 60f;

        if (targetX + targetRadius >= getWidth()) {
            targetX = getWidth() - targetRadius;
            targetSpeedX = -Math.abs(targetSpeedX);
        } else if (targetX - targetRadius <= 0) {
            targetX = targetRadius;
            targetSpeedX = Math.abs(targetSpeedX);
        }

        // =========================
        // CRONÔMETRO
        // =========================

        if (!gameOver) {

            long elapsed =
                    System.currentTimeMillis()
                            - gameStartTime;

            timeRemaining =
                    GAME_DURATION - elapsed;


            if (timeRemaining <= 0) {

                timeRemaining = 0;

                gameOver = true;

                bulletActive = false;

                return;
            }
        }


        // Quando acaba, tudo para
        if (gameOver) {

            return;
        }


        // =========================
        // BLOCKER
        // =========================
        blockerX += blockerSpeed;
<<<<<<< Updated upstream
        if (blockerX + blockerWidth >= getWidth()) {
            blockerX = getWidth() - blockerWidth;
            blockerSpeed = -Math.abs(blockerSpeed);
        } else if (blockerX <= 0) {
            blockerX = 0;
            blockerSpeed = Math.abs(blockerSpeed);
=======


        if (blockerX + blockerWidth
                >= getWidth()) {

            blockerX =
                    getWidth()
                            - blockerWidth;

            blockerSpeed =
                    -Math.abs(
                            blockerSpeed
                    );
        }


        if (blockerX <= 0) {

            blockerX = 0;

            blockerSpeed =
                    Math.abs(
                            blockerSpeed
                    );
>>>>>>> Stashed changes
        }

        // =========================
<<<<<<< Updated upstream
        // MOVIMENTO DO TIRO E COLISÕES
=======
        // TIRO
>>>>>>> Stashed changes
        // =========================
        if (bulletActive) {
<<<<<<< Updated upstream
            bulletX += bulletSpeedX;
            bulletY += bulletSpeedY;
=======

            bulletX +=
                    bulletSpeedX;

            bulletY +=
                    bulletSpeedY;
>>>>>>> Stashed changes

            RectF blockerRect = new RectF(blockerX, blockerY, blockerX + blockerWidth, blockerY + blockerHeight);
            RectF bulletRect = new RectF(bulletX - bulletRadius, bulletY - bulletRadius, bulletX + bulletRadius, bulletY + bulletRadius);

<<<<<<< Updated upstream
            // COLISÃO COM BLOCKER
            if (RectF.intersects(blockerRect, bulletRect)) {
                bulletActive = false;
                if (soundPool != null) {
                    soundPool.play(blockerHitSound, 1.0f, 1.0f, 1, 0, 1.0f);
                }
                return;
            }

            // COLISÃO COM ALVO
            float dx = bulletX - targetX;
            float dy = bulletY - targetY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
=======
            // =========================
            // BLOCKER x TIRO
            // =========================

            RectF blockerRect =
                    new RectF(
                            blockerX,
                            blockerY,
                            blockerX
                                    + blockerWidth,
                            blockerY
                                    + blockerHeight
                    );


            RectF bulletRect =
                    new RectF(
                            bulletX
                                    - bulletRadius,
                            bulletY
                                    - bulletRadius,
                            bulletX
                                    + bulletRadius,
                            bulletY
                                    + bulletRadius
                    );


            if (RectF.intersects(
                    blockerRect,
                    bulletRect)) {

                bulletActive = false;

                playSound(
                        blockerHitSound
                );

                return;
            }


            // =========================
            // ALVO x TIRO
            // =========================

            float dx =
                    bulletX
                            - targetX;

            float dy =
                    bulletY
                            - targetY;


            float distance =
                    (float) Math.sqrt(
                            dx * dx
                                    + dy * dy
                    );


            if (distance <
                    targetRadius
                            + bulletRadius) {
>>>>>>> Stashed changes

            if (distance < targetRadius + bulletRadius) {
                bulletActive = false;
                score++;

<<<<<<< Updated upstream
                // Gera explosão de partículas
                for (int i = 0; i < 20; i++) {
                    particles.add(new Particle(targetX, targetY));
                }

                // Dificuldade progressiva
                blockerSpeed += (blockerSpeed > 0) ? 0.8f : -0.8f;
                if (targetRadius > 20) targetRadius -= 2;

                if (soundPool != null) {
                    soundPool.play(targetHitSound, 1.0f, 1.0f, 1, 0, 1.0f);
                }

                float margin = targetRadius + 20;
                float availableWidth = getWidth() - (margin * 2);
                if (availableWidth > 0) {
                    targetX = margin + (float) Math.random() * availableWidth;
                }
                targetBaseY = 120 + (float) Math.random() * 250;
                targetSpeedX += (targetSpeedX > 0) ? 0.5f : -0.5f;
=======
                playSound(
                        targetHitSound
                );

                moveTarget();
>>>>>>> Stashed changes

                return;
            }

<<<<<<< Updated upstream
            // TIRO SAIU DA TELA
            if (bulletX < -bulletRadius || bulletX > getWidth() + bulletRadius || bulletY < -bulletRadius || bulletY > getHeight() + bulletRadius) {
=======

            // =========================
            // FORA DA TELA
            // =========================

            if (bulletX <
                    -bulletRadius ||

                    bulletX >
                            getWidth()
                                    + bulletRadius ||

                    bulletY <
                            -bulletRadius ||

                    bulletY >
                            getHeight()
                                    + bulletRadius) {

>>>>>>> Stashed changes
                bulletActive = false;
            }
        }
    }

    // ==================================================
    // NOVA POSIÇÃO DO ALVO
    // ==================================================

    private void moveTarget() {

        float margin =
                targetRadius + 20;

        float availableWidth =
                getWidth()
                        - margin * 2;


        if (availableWidth > 0) {

            targetX =
                    margin
                            + (float)
                            Math.random()
                            * availableWidth;
        }


        // Mantém o alvo acima do blocker
        float maximumTargetY =
                blockerY
                        - targetRadius
                        - 80;


        if (maximumTargetY > 150) {

            targetY =
                    120
                            + (float)
                            Math.random()
                            * (maximumTargetY
                            - 120);

        } else {

            targetY = 200;
        }
    }


    // ==================================================
    // DESENHO
    // ==================================================
    @Override
<<<<<<< Updated upstream
    protected void onDraw(Canvas canvas) {
=======
    protected void onDraw(
            Canvas canvas) {

>>>>>>> Stashed changes
        super.onDraw(canvas);

        // Fundo
        canvas.drawColor(Color.rgb(25, 35, 50));

<<<<<<< Updated upstream
        // HUD
        paint.setColor(Color.WHITE);
=======
        // Fundo
        canvas.drawColor(
                Color.rgb(
                        25,
                        35,
                        50
                )
        );


        // =========================
        // PONTOS
        // =========================

        paint.setColor(
                Color.WHITE
        );

>>>>>>> Stashed changes
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
            paint.setTextAlign(Paint.Align.LEFT);
            return;
        }

        // Partículas
        for (Particle p : particles) {
            paint.setColor(p.color);
            paint.setAlpha((int) (255 * ((float) p.life / p.maxLife)));
            canvas.drawCircle(p.x, p.y, p.radius, paint);
        }
        paint.setAlpha(255); // Reseta a transparência para os próximos elementos

<<<<<<< Updated upstream
        // Alvo
        paint.setColor(Color.RED);
        canvas.drawCircle(targetX, targetY, targetRadius, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(targetX, targetY, 15, paint);
=======
        // =========================
        // TEMPO
        // =========================

        long seconds =
                (timeRemaining + 999)
                        / 1000;


        String timeText =
                "Tempo: "
                        + seconds
                        + "s";


        paint.setTextSize(40);

        float timeWidth =
                paint.measureText(
                        timeText
                );


        canvas.drawText(
                timeText,
                getWidth()
                        - timeWidth
                        - 30,
                60,
                paint
        );


        // =========================
        // ALVO
        // =========================

        paint.setColor(
                Color.RED
        );

        canvas.drawCircle(
                targetX,
                targetY,
                targetRadius,
                paint
        );


        paint.setColor(
                Color.WHITE
        );
>>>>>>> Stashed changes

        // Blocker
        paint.setColor(Color.rgb(255, 140, 0));
        canvas.drawRoundRect(blockerX, blockerY, blockerX + blockerWidth, blockerY + blockerHeight, 15, 15, paint);

        // Linha de Mira
        if (isAiming) {
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(5);
            canvas.drawLine(cannonX, cannonY, aimX, aimY, paint);
            paint.setStrokeWidth(0);
        }

<<<<<<< Updated upstream
        // Canhão
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(cannonX - 35, cannonY - 20, cannonX + 35, cannonY + 30, paint);
=======
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
                blockerX
                        + blockerWidth,
                blockerY
                        + blockerHeight,
                15,
                15,
                paint
        );


        // =========================
        // CANHÃO
        // =========================

        paint.setColor(
                Color.DKGRAY
        );

>>>>>>> Stashed changes

        // Tiro
        if (bulletActive) {
<<<<<<< Updated upstream
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(bulletX, bulletY, bulletRadius, paint);
=======

            paint.setColor(
                    Color.YELLOW
            );


            canvas.drawCircle(
                    bulletX,
                    bulletY,
                    bulletRadius,
                    paint
            );
>>>>>>> Stashed changes
        }


        // =========================
        // GAME OVER
        // =========================

        if (gameOver) {

            drawGameOver(
                    canvas
            );
        }
    }


    // ==================================================
    // TELA GAME OVER
    // ==================================================

    private void drawGameOver(
            Canvas canvas) {

        // Fundo transparente escuro
        paint.setColor(
                Color.argb(
                        190,
                        0,
                        0,
                        0
                )
        );


        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                paint
        );


        // GAME OVER
        paint.setColor(
                Color.WHITE
        );

        paint.setTextSize(70);

        paint.setFakeBoldText(true);


        String gameOverText =
                "GAME OVER";


        float gameOverWidth =
                paint.measureText(
                        gameOverText
                );


        canvas.drawText(
                gameOverText,
                (getWidth()
                        - gameOverWidth)
                        / 2f,
                getHeight()
                        / 2f
                        - 80,
                paint
        );


        // Pontuação final
        paint.setTextSize(45);


        String scoreText =
                "Pontuacao final: "
                        + score;


        float scoreWidth =
                paint.measureText(
                        scoreText
                );


        canvas.drawText(
                scoreText,
                (getWidth()
                        - scoreWidth)
                        / 2f,
                getHeight()
                        / 2f,
                paint
        );


        // Reiniciar
        paint.setTextSize(35);

        paint.setFakeBoldText(false);


        String restartText =
                "Toque para jogar novamente";


        float restartWidth =
                paint.measureText(
                        restartText
                );


        canvas.drawText(
                restartText,
                (getWidth()
                        - restartWidth)
                        / 2f,
                getHeight()
                        / 2f
                        + 80,
                paint
        );
    }

    // ==================================================
    // TOQUE E SWIPE (NOVO)
    // ==================================================
    @Override
<<<<<<< Updated upstream
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            targetSpeedX = 4f;
            targetBaseY = 250;
            score = 0;
            targetRadius = 50;
            blockerSpeed = 5;
            particles.clear();
            startTime = System.currentTimeMillis();
            isGameOver = false;
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isAiming = true;
                aimX = event.getX();
                aimY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                aimX = event.getX();
                aimY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isAiming = false;
                shoot();
                break;
        }
=======
    public boolean onTouchEvent(
            MotionEvent event) {


        if (event.getAction()
                == MotionEvent.ACTION_DOWN) {


            // =========================
            // REINICIAR
            // =========================

            if (gameOver) {

                startNewGame();

                return true;
            }


            aimX =
                    event.getX();

            aimY =
                    event.getY();


            shoot();

            return true;
        }


>>>>>>> Stashed changes
        return true;
    }

    // ==================================================
    // DISPARO
    // ==================================================
    private void shoot() {
<<<<<<< Updated upstream
        if (bulletActive) return;

        bulletX = cannonX;
        bulletY = cannonY;
=======

        if (gameOver ||
                bulletActive) {

            return;
        }


        bulletX =
                cannonX;

        bulletY =
                cannonY;
>>>>>>> Stashed changes

        float dx = aimX - cannonX;
        float dy = aimY - cannonY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;

<<<<<<< Updated upstream
        bulletSpeedX = (dx / distance) * 20;
        bulletSpeedY = (dy / distance) * 20;
        bulletActive = true;

        if (soundPool != null) {
            soundPool.play(cannonFireSound, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void pause() {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
=======
        float dx =
                aimX
                        - cannonX;

        float dy =
                aimY
                        - cannonY;


        float distance =
                (float) Math.sqrt(
                        dx * dx
                                + dy * dy
                );


        if (distance == 0) {

            distance = 1;
        }


        bulletSpeedX =
                (dx / distance)
                        * 20;

        bulletSpeedY =
                (dy / distance)
                        * 20;


        bulletActive = true;


        playSound(
                cannonFireSound
        );
    }


    // ==================================================
    // SOM
    // ==================================================

    private void playSound(
            int soundId) {

        if (soundPool != null) {

            soundPool.play(
                    soundId,
                    1.0f,
                    1.0f,
                    1,
                    0,
                    1.0f
            );
        }
    }


    // ==================================================
    // PAUSE
    // ==================================================

    public void pause() {

        stopGameThread();
    }


    // ==================================================
    // RESUME
    // ==================================================

    public void resume() {

        if (getHolder()
                .getSurface()
                .isValid()) {

            if (gameThread == null ||
                    !gameThread.isAlive()) {

                gameThread =
                        new GameThread(
                                getHolder()
                        );

                gameThread.setRunning(
                        true
                );

                gameThread.start();
>>>>>>> Stashed changes
            }
        }
    }

<<<<<<< Updated upstream
    public void resume() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(getHolder());
            gameThread.setRunning(true);
            gameThread.start();
=======

    // ==================================================
    // PARAR THREAD
    // ==================================================

    private void stopGameThread() {

        if (gameThread != null) {

            gameThread.setRunning(
                    false
            );


            try {

                gameThread.join();

            } catch (
                    InterruptedException e) {

                Thread.currentThread()
                        .interrupt();
            }


            gameThread = null;
>>>>>>> Stashed changes
        }
    }

    // ==================================================
    // GAME LOOP E CLASSE DE PARTÍCULAS
    // ==================================================
<<<<<<< Updated upstream
    private class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private boolean running;

        public GameThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
=======

    private class GameThread
            extends Thread {


        private final SurfaceHolder
                surfaceHolder;


        private volatile boolean
                running;


        public GameThread(
                SurfaceHolder
                        surfaceHolder) {

            this.surfaceHolder =
                    surfaceHolder;
>>>>>>> Stashed changes
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes
            while (running) {
                Canvas canvas = null;
<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        synchronized (surfaceHolder) {
                            update();
<<<<<<< Updated upstream
                            onDraw(canvas);
                        }
                    }
                } finally {
=======

                            onDraw(
                                    canvas
                            );
                        }
                    }


                } finally {


>>>>>>> Stashed changes
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
<<<<<<< Updated upstream
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
=======

                    Thread.sleep(
                            16
                    );


                } catch (
                        InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

>>>>>>> Stashed changes
                    return;
                }
            }
        }
    }

    // Classe auxiliar para as partículas da explosão
    private class Particle {
        float x, y;
        float vx, vy;
        int life, maxLife;
        int color;
        float radius;

        Particle(float startX, float startY) {
            this.x = startX;
            this.y = startY;
            this.vx = (float) (Math.random() * 10 - 5);
            this.vy = (float) (Math.random() * 10 - 5);
            this.maxLife = (int) (Math.random() * 20 + 20);
            this.life = maxLife;
            this.radius = (float) (Math.random() * 5 + 3);

            int[] colors = {Color.YELLOW, Color.RED, Color.rgb(255, 165, 0), Color.WHITE};
            this.color = colors[(int) (Math.random() * colors.length)];
        }

        void update() {
            x += vx;
            y += vy;
            life--;
        }
    }
}