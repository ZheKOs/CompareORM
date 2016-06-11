package io.github.zhekos.compareorm;


import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.zhekos.compareorm.interfaces.IAddressBook;
import io.github.zhekos.compareorm.interfaces.IAddressItem;
import io.github.zhekos.compareorm.interfaces.IContact;

public class Generator {

    public static final long PHONE = 7185555555L;
    public static final String ADDR = "5486 Memory Lane";

    public static <AddressItem extends IAddressItem> Collection<AddressItem> getAddresses(Class<AddressItem> itemClass, int count) {
        return getAddresses(itemClass, count, null, 0, true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <AddressItem extends IAddressItem, AddressBook extends IAddressBook<AddressItem, IContact>> List<AddressItem>
    getAddresses(Class<AddressItem> itemClass, int count, AddressBook addressBook, int book_i, boolean assignBook) {
        List<AddressItem> modelList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AddressItem model;
            try {
                model = itemClass.newInstance();
                model.setName("Addr:" + i + "_Book:" + book_i);
                model.setAddress(ADDR);
                model.setCity("Bronx");
                model.setState("NY");
                model.setPhone(PHONE);
                if (assignBook && addressBook != null) {
                    model.setAddressBook(addressBook);
                }
                modelList.add(model);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return modelList;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <AddressBook extends IAddressBook, Contact extends IContact> List<Contact>
    getContacts(Class<Contact> contactClass, int count, AddressBook addressBook, int book_i, boolean assignBook) {
        List<Contact> modelList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Contact model;
            try {
                model = contactClass.newInstance();
                model.setName("Contact:" + i + "_Book:" + book_i);
                model.setEmail("abgrosner@gmail.com");
                if (assignBook && addressBook != null) {
                    model.setAddressBook(addressBook);
                }
                modelList.add(model);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return modelList;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <Contact extends IContact,
            AddressBook extends IAddressBook,
            AddressItem extends IAddressItem<AddressBook>> Collection<AddressBook>
    createAddressBooks(Class<AddressBook> addressBookClass, Class<Contact> contactClass, Class<AddressItem> addressItemClass, int count, boolean assignBook) {
        Collection<AddressBook> addressBooks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AddressBook addressBook;
            try {
                addressBook = addressBookClass.newInstance();
                addressBook.setName("AddrBook:"+i);
                addressBook.setAuthor("Andrew Grosner");
                addressBook.setAddresses(getAddresses(addressItemClass, count, addressBook, i, assignBook));
                addressBook.setContacts(getContacts(contactClass, count, addressBook, i, assignBook));
                addressBooks.add(addressBook);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return addressBooks;
    }
}