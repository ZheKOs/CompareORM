package io.github.zhekos.ormlitegenerator;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;

import io.github.zhekos.ormlitegenerator.compareorm.ormlite.AddressBook;
import io.github.zhekos.ormlitegenerator.compareorm.ormlite.AddressItem;
import io.github.zhekos.ormlitegenerator.compareorm.ormlite.Contact;
import io.github.zhekos.ormlitegenerator.compareorm.ormlite.SimpleAddressItem;

/**
 * OrmLite generator will look for all classes underneath us for proper annotatations
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            AddressBook.class, AddressItem.class, Contact.class, SimpleAddressItem.class
    };

    public static void main(String[] args) throws Exception {
        System.out.println("Current directory is " + new File(".").getCanonicalPath());
        writeConfigFile("ormlite_config.txt",classes);
    }
}