package io.github.zhekos.compareorm.sql;

import io.github.zhekos.compareorm.interfaces.IContact;

/**
 * Description:
 */
public class AddressItem extends SimpleAddressItem {
    public static final String NAME = AddressItem.class.getSimpleName();

    private AddressBook addressBook;

    @Override
    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }
}