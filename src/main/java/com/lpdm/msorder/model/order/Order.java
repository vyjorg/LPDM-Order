package com.lpdm.msorder.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lpdm.msorder.model.store.Store;
import com.lpdm.msorder.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kybox
 * @version 1.0
 * @since 01/12/2018
 */

@Entity
@Table(name = "order", schema = "public")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private double total;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status_id")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @JsonIgnore
    @Column(name = "store_id")
    private int storeId;

    @Transient
    private Store store;

    @NotNull
    @JsonIgnore
    @Column(name = "customer_id")
    private int customerId;

    @Transient
    private User customer;

    @ManyToOne
    @JoinColumn(name = "coupon")
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "delivery")
    private Delivery delivery;

    @Column(name = "tax_amount")
    private double taxAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
    private List<OrderedProduct> orderedProducts = new ArrayList<>();

    public Order() {
    }

    public Order(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public List<OrderedProduct> getOrderedProducts() {

        if(orderedProducts == null)
            orderedProducts = new ArrayList<>();

        return orderedProducts;
    }

    public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", total=" + total +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", payment=" + payment +
                ", storeId=" + storeId +
                ", store=" + store +
                ", customerId=" + customerId +
                ", customer=" + customer +
                ", coupon=" + coupon +
                ", delivery=" + delivery +
                ", taxAmount=" + taxAmount +
                ", orderedProducts=" + orderedProducts +
                '}';
    }
}