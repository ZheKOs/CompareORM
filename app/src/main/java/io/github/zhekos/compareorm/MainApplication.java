package io.github.zhekos.compareorm;

import android.app.Application;


import com.j256.ormlite.logger.LocalLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Description:
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(this);

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());

        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
    }

}
