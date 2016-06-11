package io.github.zhekos.compareorm.realm;


import io.github.zhekos.compareorm.interfaces.IAddressItem;
import io.realm.RealmObject;

public class SimpleAddressItem extends RealmObject implements IAddressItem<AddressBook> {

    private String name;
    private String address;
    private String city;
    private String state;
    private long phone;

    public SimpleAddressItem() {  }

    public String getName() { return name; }
    @Override
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    @Override
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    @Override
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    @Override
    public void setState(String state) { this.state = state; }

    public long getPhone() { return phone; }
    @Override
    public void setPhone(long phone) { this.phone = phone; }

    @Override
    public void setAddressBook(AddressBook addressBook) {  }

    @Override
    public void saveAll() {  }
}
