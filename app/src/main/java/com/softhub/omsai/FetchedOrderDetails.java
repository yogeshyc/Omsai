package com.softhub.omsai;

public class FetchedOrderDetails {

    /*public String cartId;
    public String id;*/
    public String name;
    public String SaleType;
    public String qty;
    public String price;
    // public String total;


    public FetchedOrderDetails(String name, String saleType, String qty, String price) {
       /* this.cartId = cartId;
        this.id = id;*/
        this.name = name;
        this.SaleType = saleType;
        this.qty = qty;
        this.price = price;
        //this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaleType() {
        return SaleType;
    }
    public void setSaleType(String saleType) {
        this.SaleType = saleType;
    }
    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

   /* public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/
}
