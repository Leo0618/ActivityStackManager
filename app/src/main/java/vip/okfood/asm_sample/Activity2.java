package vip.okfood.asm_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        setTitle(getLocalClassName());
    }

    public void jump(View view) {
        startActivity(new Intent(this, Activity3.class));
    }
}
