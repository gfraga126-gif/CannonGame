package com.example.cannongame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Paint paint;

    // Canhão
    private float cannonX;
    private float cannonY;

    // Mira
    private float aimX;
    private float aimY;

    // Tiro
    private float bulletX;
    private float bulletY;
    private float bulletSpeedX;
    private float bulletSpeedY;
    private boolean bulletActive = false;

    // Alvo
    private float targetX = 700;
    private float targetY = 250;
    private float targetRadius = 50;

    // Pontuação
    private int score = 0;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

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

        cannonX = getWidth() / 2f;
        cannonY = getHeight() - 100;

        if (bulletActive) {

            bulletX += bulletSpeedX;
            bulletY += bulletSpeedY;

            // Verifica colisão com o alvo
            float dx = bulletX - targetX;
            float dy = bulletY - targetY;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < targetRadius) {

                bulletActive = false;
                score++;

                // Novo alvo
                targetX = 100 + (float) Math.random() * (getWidth() - 200);
                targetY = 100 + (float) Math.random() * 300;
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

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // Fundo
        canvas.drawColor(Color.rgb(25, 35, 50));

        // Pontuação
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);

        canvas.drawText(
                "Pontos: " + score,
                30,
                60,
                paint
        );

        // Alvo
        paint.setColor(Color.RED);

        canvas.drawCircle(
                targetX,
                targetY,
                targetRadius,
                paint
        );

        // Centro do alvo
        paint.setColor(Color.WHITE);

        canvas.drawCircle(
                targetX,
                targetY,
                15,
                paint
        );

        // Canhão
        paint.setColor(Color.DKGRAY);

        canvas.drawRect(
                cannonX - 35,
                cannonY - 20,
                cannonX + 35,
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

        float distance = (float) Math.sqrt(
                dx * dx + dy * dy
        );

        if (distance == 0) {
            distance = 1;
        }

        bulletSpeedX = (dx / distance) * 20;
        bulletSpeedY = (dy / distance) * 20;

        bulletActive = true;
    }

    public void pause() {

        if (gameThread != null) {

            gameThread.setRunning(false);

            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {

        if (gameThread == null ||
                !gameThread.isAlive()) {

            gameThread = new GameThread(getHolder());

            gameThread.setRunning(true);
            gameThread.start();
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