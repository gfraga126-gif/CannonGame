package com.example.cannongame;

import android.content.SharedPreferences;
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
    private float targetRadius = 50;

    // =========================
    // BLOCKER
    // =========================
    private float blockerX = 100;
    private float blockerY = 500;
    private float blockerWidth = 180;
    private float blockerHeight = 45;
    private float blockerSpeed = 5;

    // =========================
    // PONTUAÇÃO E PARTÍCULAS
    // =========================
    private int score = 0;
    private int highScore = 0;
    private SharedPreferences prefs;
    private List<Particle> particles = new ArrayList<>();

    // ==================================================
    // CONSTRUTOR
    // ==================================================
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        prefs = context.getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);

        paint = new Paint();
        paint.setAntiAlias(true);

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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

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
    // UPDATE
    // ==================================================
    private void update() {
        if (isGameOver) return;

        timeLeft = totalGameTime - (System.currentTimeMillis() - startTime);
        if (timeLeft <= 0) {
            timeLeft = 0;
            isGameOver = true;

            if (score > highScore) {
                highScore = score;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highScore", highScore);
                editor.apply(); // Salva de forma assíncrona
            }
            return;

        }

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // Posição do canhão
        cannonX = getWidth() / 2f;
        cannonY = getHeight() - 100;

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
        // BLOCKER
        // =========================
        blockerX += blockerSpeed;
        if (blockerX + blockerWidth >= getWidth()) {
            blockerX = getWidth() - blockerWidth;
            blockerSpeed = -Math.abs(blockerSpeed);
        } else if (blockerX <= 0) {
            blockerX = 0;
            blockerSpeed = Math.abs(blockerSpeed);
        }

        // =========================
        // MOVIMENTO DO TIRO E COLISÕES
        // =========================
        if (bulletActive) {
            bulletX += bulletSpeedX;
            bulletY += bulletSpeedY;

            RectF blockerRect = new RectF(blockerX, blockerY, blockerX + blockerWidth, blockerY + blockerHeight);
            RectF bulletRect = new RectF(bulletX - bulletRadius, bulletY - bulletRadius, bulletX + bulletRadius, bulletY + bulletRadius);

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

            if (distance < targetRadius + bulletRadius) {
                bulletActive = false;
                score++;

                startTime += 5000;

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

                return;
            }

            // TIRO SAIU DA TELA
            if (bulletX < -bulletRadius || bulletX > getWidth() + bulletRadius || bulletY < -bulletRadius || bulletY > getHeight() + bulletRadius) {
                bulletActive = false;
            }
        }
    }

    // ==================================================
    // DESENHO
    // ==================================================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fundo
        canvas.drawColor(Color.rgb(25, 35, 50));

        // HUD
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);
        canvas.drawText("Pontos: " + score, 30, 60, paint);
        canvas.drawText("Recorde: " + highScore, 30, 110, paint);
        canvas.drawText("Tempo: " + (timeLeft / 1000) + "s", getWidth() - 300, 60, paint);

        if (isGameOver) {
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;

            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME OVER", centerX, centerY - 120, paint);

            paint.setTextSize(50);
            canvas.drawText("Pontuação final: " + score, centerX, centerY - 30, paint);


            if (score >= highScore && score > 0) {
                long currentTime = System.currentTimeMillis();
                float scale = 1.0f + 0.15f * (float) Math.sin(currentTime / 150.0);

                canvas.save();
                canvas.scale(scale, scale, centerX, centerY + 50);

                if (currentTime % 400 < 200) {
                    paint.setColor(Color.YELLOW);
                } else {
                    paint.setColor(Color.rgb(255, 140, 0));
                }

                canvas.drawText("NOVO RECORDE!", centerX, centerY + 50, paint);
                canvas.restore();

            } else {
                canvas.drawText("Recorde atual: " + highScore, centerX, centerY + 50, paint);
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            canvas.drawText("Toque para jogar novamente", centerX, centerY + 150, paint);

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

        // Alvo
        paint.setColor(Color.RED);
        canvas.drawCircle(targetX, targetY, targetRadius, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(targetX, targetY, 15, paint);

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

        // Canhão
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(cannonX - 35, cannonY - 20, cannonX + 35, cannonY + 30, paint);

        // Tiro
        if (bulletActive) {
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(bulletX, bulletY, bulletRadius, paint);
        }
    }

    // ==================================================
    // TOQUE E SWIPE
    // ==================================================
    @Override
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
        return true;
    }

    // ==================================================
    // DISPARO
    // ==================================================
    private void shoot() {
        if (bulletActive) return;

        bulletX = cannonX;
        bulletY = cannonY;

        float dx = aimX - cannonX;
        float dy = aimY - cannonY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;

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
            }
        }
    }

    public void resume() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new GameThread(getHolder());
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    // ==================================================
    // GAME LOOP E CLASSE DE PARTÍCULAS
    // ==================================================
    private class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
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
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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