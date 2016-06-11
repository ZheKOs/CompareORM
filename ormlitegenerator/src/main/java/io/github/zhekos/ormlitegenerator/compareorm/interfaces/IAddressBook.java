package io.github.zhekos.ormlitegenerator.compareorm.interfaces;

import java.util.Collection;

/**
 * Description:
 */
public interface IAddressBook<AddressItem extends IAddressItem, Contact extends IContact> extends ISaveable {

    public void setName(String name);

    public void setAuthor(String author);

    public void setAddresses(Collection<AddressItem> addresses);

    public Collection<AddressItem> getAddresses();

    public Collection<Contact> getContacts();

    public void setContacts(Collection<Contact> contacts);
}
