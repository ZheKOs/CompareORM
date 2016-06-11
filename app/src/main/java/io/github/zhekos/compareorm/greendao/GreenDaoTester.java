package io.github.zhekos.compareorm.greendao;

import android.content.Context;


import java.util.Collection;

import de.greenrobot.event.EventBus;
import io.github.zhekos.compareorm.Generator;
import io.github.zhekos.compareorm.MainActivity;
import io.github.zhekos.compareorm.Verificator;
import io.github.zhekos.compareorm.events.LogTestDataEvent;
import io.github.zhekos.compareorm.greendao.gen.AddressBook;
import io.github.zhekos.compareorm.greendao.gen.AddressBookDao;
import io.github.zhekos.compareorm.greendao.gen.AddressItem;
import io.github.zhekos.compareorm.greendao.gen.AddressItemDao;
import io.github.zhekos.compareorm.greendao.gen.Contact;
import io.github.zhekos.compareorm.greendao.gen.ContactDao;
import io.github.zhekos.compareorm.greendao.gen.DaoMaster;
import io.github.zhekos.compareorm.greendao.gen.DaoSession;
import io.github.zhekos.compareorm.greendao.gen.SimpleAddressItem;
import io.github.zhekos.compareorm.greendao.gen.SimpleAddressItemDao;

/**
 * Description:
 */
public class GreenDaoTester {
    public static final String FRAMEWORK_NAME = "GreenDAO";

    public static void testAddressItems(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        final SimpleAddressItemDao simpleAddressItemDao = daoSession.getSimpleAddressItemDao();
        simpleAddressItemDao.deleteAll();

        final Collection<SimpleAddressItem> addressItemList = Generator.getAddresses(SimpleAddressItem.class, MainActivity.SIMPLE_LOOP_COUNT);
        long startTime = System.currentTimeMillis();
        simpleAddressItemDao.insertOrReplaceInTx(addressItemList);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.SAVE_TIME));
        helper.close();
    }

    public static void testAddressBooks(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        final DaoSession daoSession = daoMaster.newSession();
        final AddressBookDao addressBookDao = daoSession.getAddressBookDao();
        addressBookDao.deleteAll();
        daoSession.getAddressItemDao().deleteAll();
        daoSession.getContactDao().deleteAll();

        final Collection<AddressBook> addressBooks = Generator.createAddressBooks(AddressBook.class, Contact.class, AddressItem.class, MainActivity.COMPLEX_LOOP_COUNT, false);
        long startTime = System.currentTimeMillis();

        final ContactDao contactsDao = daoSession.getContactDao();
        final AddressItemDao addrDao = daoSession.getAddressItemDao();

        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {

                for (AddressBook book : addressBooks) {
                    addressBookDao.insert(book);
                    for (AddressItem addr : book.getAddresses()) {
                        addr.setAddressBook(book);
                        addrDao.insert(addr);
                    }
                    for (Contact contact : book.getContacts()) {
                        contact.setAddressBook(book);
                        contactsDao.insert(contact);
                    }
                }
            }
        });

        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.SAVE_TIME));
        helper.close();
    }

    public static void testAddressItemsRead(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        final SimpleAddressItemDao simpleAddressItemDao = daoSession.getSimpleAddressItemDao();

        long startTime = System.currentTimeMillis();
        Collection<SimpleAddressItem> addressItems = simpleAddressItemDao.loadAll();
        Verificator.verifySimple(FRAMEWORK_NAME, addressItems);
        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.LOAD_TIME));
        helper.close();
    }

    public static void testAddressBooksRead(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookDao addressBookDao = daoSession.getAddressBookDao();

        long startTime = System.currentTimeMillis();
        Collection<AddressBook> addressBooks = addressBookDao.loadAll();
        Verificator.verify(FRAMEWORK_NAME, addressBooks);

        EventBus.getDefault().post(new LogTestDataEvent(startTime, System.currentTimeMillis(), FRAMEWORK_NAME, MainActivity.LOAD_TIME));
        helper.close();
    }
}
