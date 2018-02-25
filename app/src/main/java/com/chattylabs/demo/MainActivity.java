package com.chattylabs.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chattylabs.demo.awareness.R;
import com.chattylabs.module.awareness.AwarenessComponent;
import com.chattylabs.module.awareness.AwarenessComponentImpl;
import com.chattylabs.module.base.CsvWriter;
import com.chattylabs.module.base.PermissionsHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test).setOnClickListener(v -> {
            new AwarenessComponentImpl(getApplication(), GeneralIntentService.class)
                    .register(this, new int[]{
                            AwarenessComponent.Fences.STARTING_ON_FOOT,
                            AwarenessComponent.Fences.STOPPING_ON_FOOT
                    });
        });

        PermissionsHelper.check(this, new CsvWriter().requiredPermissions());
    }
}
