package io.github.zhekos.compareorm;

import java.util.Collection;

import io.github.zhekos.compareorm.interfaces.ISaveable;

/**
 * Description:
 */
public class Saver {

    public static void saveAll(Collection<? extends ISaveable> saveables) {
        for(ISaveable saveable: saveables) {
            saveable.saveAll();
        }
    }
}
