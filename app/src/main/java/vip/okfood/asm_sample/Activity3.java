package vip.okfood.asm_sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import vip.okfood.asm.ActivityStackManager;

public class Activity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        setTitle(getLocalClassName());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityStackManager.get().finishActivity(Activity2.class);
                Toast.makeText(Activity3.this, "已关闭Activity2,返回会直接回到Activity1", Toast.LENGTH_LONG).show();
            }
        }, 2000);
    }
}
