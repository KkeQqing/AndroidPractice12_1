package com.example.androidpractice12_1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private Button btnStart;
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);
        tvScore = findViewById(R.id.tvScore);

        // 设置游戏状态监听
        gameView.setOnGameStatusListener(new GameView.OnGameStatusListener() {
            @Override
            public void onScoreChanged(int score) {
            }

            @Override
            public void onGameOver(int finalScore) {
                btnStart.setText("重玩");
                btnStart.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Boom! 游戏结束", Toast.LENGTH_SHORT).show();
            }
        });

        // 按钮点击事件
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.startGame();
                btnStart.setVisibility(View.GONE); // 游戏开始后隐藏按钮
            }
        });
    }

    // 处理返回键或退出，确保停止游戏循环
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.stopGame();
    }
}