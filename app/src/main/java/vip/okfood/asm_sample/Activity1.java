package vip.okfood.asm_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        setTitle(getLocalClassName());
    }

    public void jump(View view) {
        startActivity(new Intent(this, Activity2.class));
    }

    Intent mAliveService;

    public void startAndStopService(View view) {
        if(mAliveService != null) {
            stopService(mAliveService); mAliveService = null;
        } else {
            startService(mAliveService = new Intent(this, AliveService.class));
        }
    }
}
