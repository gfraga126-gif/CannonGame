package com.example.cannongame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import java.util.Random;
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
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] starX = new float[130];
    private final float[] starY = new float[130];
    private final float[] starSize = new float[130];

    // =========================
    // ESTADOS DO JOGO
    // =========================
    private boolean isMainMenu = true;
    private boolean isGameOver = false;

    // =========================
    // VARIÁVEIS DE TEMPO E PONTOS
    // =========================
    private long startTime;
    private long timeLeft;
    private final long totalGameTime = 60000;
    private int score = 0;
    // Exibe a pontuacao no centro por 1,2 segundo apos acertar o alvo
    private long pointPopupStart = 0L;
    private static final long POINT_POPUP_DURATION = 1200L;
    private int highScore = 0;
    private SharedPreferences prefs;

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
    // CANHÃO E MIRA
    // =========================
    private float cannonX;
    private float cannonY;
    private float aimX;
    private float aimY;
    private boolean isAiming = false;
    private float cannonAngle = -90f;

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
    // PARTÍCULAS
    // =========================
    private List<Particle> particles = new ArrayList<>();

    // ==================================================
    // CONSTRUTOR
    // ==================================================
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        // Inicializa o SharedPreferences e carrega o recorde
        prefs = context.getSharedPreferences("CannonGamePrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);

        paint = new Paint();
        paint.setAntiAlias(true);

        // Estrelas fixas: não mudam de posição a cada quadro.
        Random starRandom = new Random(2026);
        for (int i = 0; i < starX.length; i++) {
            starX[i] = starRandom.nextFloat();
            starY[i] = starRandom.nextFloat();
            starSize[i] = 0.8f + starRandom.nextFloat() * 2.2f;
        }

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
                Thread.currentThread().interrupt();
            }
        }
    }

    // ==================================================
    // ATUALIZAÇÃO DO JOGO
    // ==================================================
    private void update() {
        if (isMainMenu) return;
        if (isGameOver) return;

        timeLeft = totalGameTime - (System.currentTimeMillis() - startTime);
        if (timeLeft <= 0) {
            timeLeft = 0;
            isGameOver = true;

            // Salva o novo recorde se necessário
            if (score > highScore) {
                highScore = score;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highScore", highScore);
                editor.apply();
            }
            return;
        }

        if (getWidth() <= 0 || getHeight() <= 0) return;

        cannonX = getWidth() / 2f;
        cannonY = getHeight() - 100;

        // Atualiza Partículas
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (p.life <= 0) {
                iterator.remove();
            }
        }

        // Movimento do Alvo
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

        // Movimento do Blocker
        blockerX += blockerSpeed;
        if (blockerX + blockerWidth >= getWidth()) {
            blockerX = getWidth() - blockerWidth;
            blockerSpeed = -Math.abs(blockerSpeed);
        } else if (blockerX <= 0) {
            blockerX = 0;
            blockerSpeed = Math.abs(blockerSpeed);
        }

        // Movimento e Colisão do Tiro
        if (bulletActive) {
            bulletX += bulletSpeedX;
            bulletY += bulletSpeedY;

            RectF blockerRect = new RectF(blockerX, blockerY, blockerX + blockerWidth, blockerY + blockerHeight);
            RectF bulletRect = new RectF(bulletX - bulletRadius, bulletY - bulletRadius, bulletX + bulletRadius, bulletY + bulletRadius);

            // Colisão com Blocker
            if (RectF.intersects(blockerRect, bulletRect)) {
                bulletActive = false;
                if (soundPool != null) soundPool.play(blockerHitSound, 1.0f, 1.0f, 1, 0, 1.0f);
                return;
            }

            // Colisão com Alvo
            float dx = bulletX - targetX;
            float dy = bulletY - targetY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < targetRadius + bulletRadius) {
                bulletActive = false;
                score++;
                pointPopupStart = System.currentTimeMillis();
                startTime += 5000; // Bônus de +5 segundos

                for (int i = 0; i < 20; i++) particles.add(new Particle(targetX, targetY));

                blockerSpeed += (blockerSpeed > 0) ? 0.8f : -0.8f;
                if (targetRadius > 20) targetRadius -= 2;

                if (soundPool != null) soundPool.play(targetHitSound, 1.0f, 1.0f, 1, 0, 1.0f);

                float margin = targetRadius + 20;
                float availableWidth = getWidth() - (margin * 2);
                if (availableWidth > 0) targetX = margin + (float) Math.random() * availableWidth;

                targetBaseY = 120 + (float) Math.random() * 250;
                targetSpeedX += (targetSpeedX > 0) ? 0.5f : -0.5f;
                return;
            }

            // Saiu da tela
            if (bulletX < -bulletRadius || bulletX > getWidth() + bulletRadius || bulletY < -bulletRadius || bulletY > getHeight() + bulletRadius) {
                bulletActive = false;
            }
        }
    }

    // ==================================================
    // FUNDO ESPACIAL (APENAS VISUAL)
    // ==================================================
    private void drawSpaceBackground(Canvas canvas) {
        float w = canvas.getWidth();
        float h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        backgroundPaint.setShader(new LinearGradient(0, 0, 0, h,
                Color.rgb(5, 8, 31), Color.rgb(37, 16, 76), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, backgroundPaint);
        backgroundPaint.setShader(null);

        // Nebulosas suaves no espaço.
        backgroundPaint.setColor(Color.argb(30, 115, 75, 200));
        canvas.drawOval(-w * 0.25f, h * 0.10f, w * 0.70f, h * 0.80f, backgroundPaint);
        backgroundPaint.setColor(Color.argb(23, 35, 135, 210));
        canvas.drawOval(w * 0.40f, -h * 0.30f, w * 1.18f, h * 0.45f, backgroundPaint);

        // Estrelas distribuídas proporcionalmente à tela.
        for (int i = 0; i < starX.length; i++) {
            int alpha = 130 + (i % 5) * 25;
            backgroundPaint.setColor(Color.argb(alpha, 235, 241, 255));
            canvas.drawCircle(starX[i] * w, starY[i] * h, starSize[i], backgroundPaint);
        }

        // Planeta distante à direita.
        float px = w * 0.83f;
        float py = h * 0.28f;
        float radius = Math.min(w, h) * 0.12f;
        backgroundPaint.setColor(Color.rgb(68, 89, 176));
        canvas.drawCircle(px, py, radius, backgroundPaint);
        backgroundPaint.setColor(Color.argb(120, 154, 179, 242));
        canvas.drawCircle(px - radius * 0.22f, py - radius * 0.23f,
                radius * 0.62f, backgroundPaint);

        // Pequena lua na parte esquerda.
        float moonRadius = Math.min(w, h) * 0.043f;
        backgroundPaint.setColor(Color.rgb(162, 181, 204));
        canvas.drawCircle(w * 0.14f, h * 0.22f, moonRadius, backgroundPaint);
        backgroundPaint.setColor(Color.argb(90, 75, 91, 128));
        canvas.drawCircle(w * 0.13f, h * 0.21f, moonRadius * 0.23f, backgroundPaint);
    }

    // ==================================================
    // DESENHO
    // ==================================================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSpaceBackground(canvas); // Fundo espacial

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // MENU INICIAL
        if (isMainMenu) {
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(110);
            paint.setColor(Color.YELLOW);
            paint.setFakeBoldText(true);
            canvas.drawText("CANNON GAME", centerX, centerY - 120, paint);

            paint.setTextSize(55);
            paint.setColor(Color.WHITE);
            paint.setFakeBoldText(false);
            canvas.drawText("Recorde atual: " + highScore, centerX, centerY, paint);

            paint.setTextSize(40);
            canvas.drawText("Toque na tela para iniciar", centerX, centerY + 120, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            return;
        }

        // HUD DA PARTIDA
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);
        canvas.drawText("Pontos: " + score, 30, 60, paint);
        canvas.drawText("Recorde: " + highScore, 30, 110, paint);
        canvas.drawText("Tempo: " + (timeLeft / 1000) + "s", getWidth() - 300, 60, paint);

        // GAME OVER
        if (isGameOver) {
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
                if (currentTime % 400 < 200) paint.setColor(Color.YELLOW);
                else paint.setColor(Color.rgb(255, 140, 0));

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

        // PARTÍCULAS
        for (Particle p : particles) {
            paint.setColor(p.color);
            paint.setAlpha((int) (255 * ((float) p.life / p.maxLife)));
            canvas.drawCircle(p.x, p.y, p.radius, paint);
        }
        paint.setAlpha(255);

        // ALVO
        paint.setColor(Color.RED);
        canvas.drawCircle(targetX, targetY, targetRadius, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(targetX, targetY, 15, paint);

        // BLOCKER
        paint.setColor(Color.rgb(255, 140, 0));
        canvas.drawRoundRect(blockerX, blockerY, blockerX + blockerWidth, blockerY + blockerHeight, 15, 15, paint);

        // LINHA DE MIRA
        if (isAiming) {
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(5);
            canvas.drawLine(cannonX, cannonY, aimX, aimY, paint);
            paint.setStrokeWidth(0);
        }

        // CANHÃO (MODELO ROTATIVO)
        paint.setColor(Color.rgb(120, 120, 120));
        canvas.drawRoundRect(cannonX - 45, cannonY + 5, cannonX + 45, cannonY + 35, 12, 12, paint);

        canvas.save();
        canvas.rotate(cannonAngle + 90, cannonX, cannonY + 5);
        paint.setColor(Color.rgb(70, 70, 70));
        canvas.drawRect(cannonX - 20, cannonY - 75, cannonX + 20, cannonY + 15, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(cannonX - 25, cannonY - 80, cannonX + 25, cannonY - 60, paint);
        canvas.restore();

        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(cannonX, cannonY + 5, 22, paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(cannonX, cannonY + 5, 10, paint);

        // TIRO
        if (bulletActive) {
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(bulletX, bulletY, bulletRadius, paint);
        }

        // Pontos em destaque no centro, com aumento suave e desaparecimento
        long elapsed = System.currentTimeMillis() - pointPopupStart;
        if (pointPopupStart > 0 && elapsed >= 0 && elapsed < POINT_POPUP_DURATION) {
            float progress = (float) elapsed / POINT_POPUP_DURATION;
            float scale = 0.85f + 0.25f * Math.min(progress * 4f, 1f);
            int alpha = progress < 0.65f ? 255 :
                    (int) (255f * (1f - progress) / 0.35f);

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            paint.setTextSize(100f * scale);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7f);
            paint.setColor(Color.BLACK);
            paint.setAlpha(alpha);
            canvas.drawText("+1 PONTO!", centerX, centerY, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.YELLOW);
            paint.setAlpha(alpha);
            canvas.drawText("+1 PONTO!", centerX, centerY, paint);
            paint.setAlpha(255);
            paint.setFakeBoldText(false);
            paint.setTextAlign(Paint.Align.LEFT);
        }

        // Contagem regressiva nos ultimos 10 segundos da partida.
        if (timeLeft > 0 && timeLeft <= 10000L) {
            int secondsRemaining = (int) ((timeLeft + 999L) / 1000L);
            float secondProgress = (timeLeft % 1000L) / 1000f;
            float pulse = 1f + 0.10f * (float) Math.sin(secondProgress * Math.PI * 2);
            float countdownY = centerY;
            // Se o jogador acertar o alvo, os dois avisos ficam visiveis.
            if (pointPopupStart > 0 && elapsed >= 0 && elapsed < POINT_POPUP_DURATION) {
                countdownY = centerY - Math.min(getHeight() * 0.23f, 170f);
            }

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            paint.setTextSize(Math.min(getWidth() * 0.20f, 160f) * pulse);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(9f);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(secondsRemaining), centerX, countdownY, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(255, 65, 65));
            canvas.drawText(String.valueOf(secondsRemaining), centerX, countdownY, paint);
            paint.setFakeBoldText(false);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }

    // ==================================================
    // TOQUE E SWIPE
    // ==================================================
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMainMenu && event.getAction() == MotionEvent.ACTION_DOWN) {
            isMainMenu = false;
            startTime = System.currentTimeMillis();
            return true;
        }

        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            targetSpeedX = 4f;
            targetBaseY = 250;
            score = 0;
            pointPopupStart = 0L;
            targetRadius = 50;
            blockerSpeed = 5;
            particles.clear();
            startTime = System.currentTimeMillis();
            isGameOver = false;
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isAiming = true;
                aimX = event.getX();
                aimY = event.getY();
                cannonAngle = (float) Math.toDegrees(Math.atan2(aimY - cannonY, aimX - cannonX));
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

        float dx = aimX - cannonX;
        float dy = aimY - cannonY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;

        bulletX = cannonX + (dx / distance) * 80;
        bulletY = cannonY + (dy / distance) * 80;

        bulletSpeedX = (dx / distance) * 20;
        bulletSpeedY = (dy / distance) * 20;
        bulletActive = true;

        if (soundPool != null) soundPool.play(cannonFireSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private int interpolateColor(int colorStart, int colorEnd, float fraction) {
        int r = (int) (Color.red(colorStart) + fraction * (Color.red(colorEnd) - Color.red(colorStart)));
        int g = (int) (Color.green(colorStart) + fraction * (Color.green(colorEnd) - Color.green(colorStart)));
        int b = (int) (Color.blue(colorStart) + fraction * (Color.blue(colorEnd) - Color.blue(colorStart)));
        return Color.rgb(r, g, b);
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
    // GAME LOOP E PARTÍCULAS
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
                    if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
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

    private class Particle {
        float x, y;
        float vx, vy;
        int life, maxLife;
        int color;
        float radius;

        Particle(float startX, float startY) {
            this.x = startX;
            this.y = startY;
            // Aumentámos a velocidade inicial (de 16 para 24) para espalharem mais longe
            this.vx = (float) (Math.random() * 24 - 12);
            this.vy = (float) (Math.random() * 24 - 12);

            // Vida curta para sumirem rapidamente logo após a expansão
            this.maxLife = (int) (Math.random() * 8 + 10);
            this.life = maxLife;
            this.radius = (float) (Math.random() * 4 + 2);

            int[] colors = {Color.YELLOW, Color.RED, Color.rgb(255, 165, 0), Color.WHITE};
            this.color = colors[(int) (Math.random() * colors.length)];
        }

        void update() {
            x += vx;
            y += vy;

            // Atrito para desacelerarem rapidamente após o impulso inicial
            vx *= 0.82f;
            vy *= 0.82f;

            life--;
        }
    }
}
