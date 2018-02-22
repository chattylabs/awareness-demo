package com.chattylabs.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chattylabs.module.awareness.AwareComponentImpl;
import com.chattylabs.module.awareness.R;
import com.chattylabs.module.base.CsvWriter;
import com.chattylabs.module.base.PermissionsHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test).setOnClickListener(v -> {
            new AwareComponentImpl(getApplication(), GeneralIntentService.class)
                    .start(getBaseContext(), AwarenessServiceDelegate
                            .getGeneralCallbackBuilder(this).build());
        });

        PermissionsHelper.check(this, new CsvWriter(this).requiredPermissions());
    }
}
