package com.example.androidpractice12_1;

import android.graphics.RectF;

public class Fruit {
    public float x, y;          // 圆心坐标
    public float radius;        // 半径
    public boolean isBomb;      // 是否是炸弹（true=黑色炸弹，false=红色西瓜）
    public float speed;         // 下落速度
    public RectF rect;          // 用于检测碰撞的矩形区域

    public Fruit(float x, float y, boolean isBomb, float speed, float radius) {
        this.x = x;
        this.y = y;
        this.isBomb = isBomb;
        this.speed = speed;
        this.radius = radius;
        this.rect = new RectF();
        updateRect();
    }

    // 更新矩形区域，用于碰撞检测
    public void updateRect() {
        rect.set(x - radius, y - radius, x + radius, y + radius);
    }
}