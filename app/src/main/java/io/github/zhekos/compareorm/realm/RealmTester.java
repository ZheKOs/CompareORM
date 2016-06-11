package io.github.zhekos.compareorm.realm;


import java.util.Collection;

import io.github.zhekos.compareorm.Generator;
import io.github.zhekos.compareorm.MainActivity;
import io.github.zhekos.compareorm.Verificator;
import io.github.zhekos.compareorm.events.LogTestDataEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import de.greenrobot.event.EventBus;


public class RealmTester {
    public static final String FRAMEWORK_NAME = "Realm";

    public static void testAddressBooks() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(AddressItem.class);
                realm.delete(AddressBook.class);
                realm.delete(Contact.class);
            }
        });

        final Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class, Contact.class, AddressItem.class, MainActivity.COMPLEX_LOOP_COUNT, true);

        long startTime = System.currentTimeMillis();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(addressBooks);
            }
        });
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.SAVE_TIME));
        realm.close();
    }

    public static void testAddressItems() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(SimpleAddressItem.class);
            }
        });

        final Collection<SimpleAddressItem> addresses = Generator.getAddresses(SimpleAddressItem.class, MainActivity.SIMPLE_LOOP_COUNT);

        long startTime = System.currentTimeMillis();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(addresses);
            }
        });
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.SAVE_TIME));
        realm.close();
    }

    public static void testAddressBooksRead() {
        Realm realm = Realm.getDefaultInstance();

        long startTime = System.currentTimeMillis();
        RealmResults<AddressBook> addressBooks = realm.where(AddressBook.class).findAll();
        Verificator.verify(FRAMEWORK_NAME, addressBooks);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.LOAD_TIME));
        realm.close();
    }

    public static void testAddressItemsRead() {
        Realm realm = Realm.getDefaultInstance();

        long startTime = System.currentTimeMillis();
        RealmResults<SimpleAddressItem> addressItems = realm.where(SimpleAddressItem.class).findAll();
        Verificator.verifySimple(FRAMEWORK_NAME, addressItems);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.LOAD_TIME));
        realm.close();
    }
}