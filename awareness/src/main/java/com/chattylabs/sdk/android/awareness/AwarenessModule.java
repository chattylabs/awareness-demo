package com.chattylabs.sdk.android.awareness;

@dagger.Module
public class AwarenessModule {
    @dagger.Provides @dagger.Reusable
    public static AwarenessComponent provideAwarenessComponent() {
        return AwarenessComponent.Instance.getInstanceOf();
    }
}
