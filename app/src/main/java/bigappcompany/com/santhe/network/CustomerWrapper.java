package bigappcompany.com.santhe.network;

import com.google.gson.annotations.SerializedName;

import bigappcompany.com.santhe.interfaces.ResponseWrapper;
import bigappcompany.com.santhe.model.Customer;

/**
 * Created by akshay on 12/8/17.
 */

public class CustomerWrapper implements ResponseWrapper<Customer> {

    @SerializedName("customer")
    private Customer customer;

    public CustomerWrapper(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Customer getContent() {
        return customer;
    }

}
