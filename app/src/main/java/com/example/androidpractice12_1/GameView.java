package com.example.androidpractice12_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends View {

    private ArrayList<Fruit> fruits;
    private Paint paintRed, paintBlack, paintText;
    private Random random;
    private boolean isPlaying = false;
    private int score = 0;
    private Handler handler;
    private Runnable gameLoop;
    private OnGameStatusListener listener; // 用于通知Activity更新UI

    // 游戏配置
    private static final int MAX_FRUITS = 10; // 屏幕上最大水果数
    private static final long GAME_DELAY = 30; // 刷新间隔 (ms)

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fruits = new ArrayList<>();
        random = new Random();

        // 初始化画笔
        paintRed = new Paint();
        paintRed.setColor(Color.RED);
        paintRed.setAntiAlias(true);

        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(60);
        paintText.setAntiAlias(true);

        handler = new Handler();

        // 游戏主循环
        gameLoop = new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    updateGame();
                    invalidate(); // 重绘
                    handler.postDelayed(this, GAME_DELAY);
                }
            }
        };
    }

    // 设置监听器，让Activity知道游戏状态变化
    public void setOnGameStatusListener(OnGameStatusListener listener) {
        this.listener = listener;
    }

    public void startGame() {
        fruits.clear();
        score = 0;
        isPlaying = true;
        if (listener != null) listener.onScoreChanged(score);
        handler.post(gameLoop);
    }

    public void stopGame() {
        isPlaying = false;
        handler.removeCallbacks(gameLoop);
    }

    private void updateGame() {
        // 1. 生成新水果/炸弹
        if (fruits.size() < MAX_FRUITS && random.nextInt(20) == 0) {
            float radius = 60 + random.nextInt(40); // 半径 60-100
            float x = radius + random.nextInt(getWidth() - (int)radius * 2);
            float y = -radius; // 从屏幕上方出现
            boolean isBomb = random.nextInt(10) < 3; // 30% 概率是炸弹
            float speed = 10 + random.nextInt(15);

            fruits.add(new Fruit(x, y, isBomb, speed, radius));
        }

        // 2. 更新位置 & 移除屏幕外的水果
        Iterator<Fruit> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            Fruit f = iterator.next();
            f.y += f.speed;
            f.updateRect();

            // 如果掉出屏幕底部，移除
            if (f.y - f.radius > getHeight()) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawColor(Color.WHITE);

        // 绘制水果/炸弹
        for (Fruit f : fruits) {
            if (f.isBomb) {
                canvas.drawCircle(f.x, f.y, f.radius, paintBlack);
            } else {
                canvas.drawCircle(f.x, f.y, f.radius, paintRed);
            }
        }

        // 绘制分数
        canvas.drawText("分数: " + score, 20, 60, paintText);

        // 绘制游戏结束提示
        if (!isPlaying && score > 0) {
            paintText.setTextSize(80);
            canvas.drawText("游戏结束", getWidth() / 2 - 200, getHeight() / 2, paintText);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPlaying) return true;

        float x = event.getX();
        float y = event.getY();

        // 简单的触摸检测：遍历所有水果，看手指是否碰到
        Iterator<Fruit> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            Fruit f = iterator.next();
            // 判断点(x,y)是否在圆内
            double distance = Math.sqrt(Math.pow(x - f.x, 2) + Math.pow(y - f.y, 2));

            if (distance < f.radius) {
                // 切到了！
                if (f.isBomb) {
                    // 炸弹 - 游戏结束
                    isPlaying = false;
                    if (listener != null) listener.onGameOver(score);
                } else {
                    // 西瓜 - 加分
                    score += 10;
                    if (listener != null) listener.onScoreChanged(score);
                    iterator.remove(); // 切掉西瓜
                }
                break; // 一次只切一个
            }
        }
        return true;
    }

    // 接口定义，用于与Activity通信
    public interface OnGameStatusListener {
        void onScoreChanged(int score);
        void onGameOver(int finalScore);
    }
}