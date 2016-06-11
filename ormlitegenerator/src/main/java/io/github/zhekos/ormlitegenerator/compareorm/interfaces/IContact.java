package io.github.zhekos.ormlitegenerator.compareorm.interfaces;

/**
 * Description:
 */
public interface IContact<AddressBook extends IAddressBook> extends ISaveable {

    public String getName();

    public void setName(String name);

    public String getEmail();

    public void setEmail(String email);

    public AddressBook getAddressBookField();

    public void setAddressBook(AddressBook addressBook);
}
