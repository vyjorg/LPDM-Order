package com.lpdm.msorder.service;

import com.lpdm.msorder.model.order.Order;
import com.lpdm.msorder.model.order.OrderedProduct;
import com.lpdm.msorder.model.order.Payment;
import com.lpdm.msorder.model.order.Status;
import com.lpdm.msorder.model.user.OrderStats;
import com.lpdm.msorder.model.user.SearchDates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * Order
     */
    Order saveOrder(Order order);
    void deleteOrder(Order order);
    Optional<Order> findOrderById(int id);
    List<Order> findAllOrdersByCustomerId(int id);
    List<Order> findAllOrdersByPayment(Payment payment);
    List<Order> findAllOrdersByCustomerEmail(String email);
    Page<Order> findAllOrdersPageable(PageRequest pageRequest);
    List<Order> findAllOrdersByCustomerLastName(String lastName);
    List<Order> findAllOrdersByCustomerIdAndStatus(int id, Status status);
    List<Order> findAllOrdersBetweenTwoDates(SearchDates searchDates);
    List<Order> findAllOrdersByStatusPageable(Status status, PageRequest pageRequest);
    List<Order> findAllOrdersByCustomerIdOrderByOrderDateAsc(int id, PageRequest pageRequest);
    List<Order> findAllOrdersByCustomerIdOrderByOrderDateDesc(int id, PageRequest pageRequest);

    /**
     * OrderedProduct
     */
    OrderedProduct saveOrderedProduct(OrderedProduct orderedProduct);
    List<OrderedProduct> getOrderedProductsByOrder(Order order);
    List<OrderedProduct> findAllOrderedProductsByProductId(int id);
    List<OrderedProduct> findAllOrderedProductsByOrder(Order order);

    /**
     * Payment
     */
    List<Payment> findAllPayments();
    Optional<Payment> findPaymentById(int id);
    Payment savePayment(Payment payment);
    void deletePayment(Payment payment);

    /**
     * Stats
     */
    OrderStats getOrderStatsByYear(int year);
    OrderStats getOrderStatsByYearAndMonth(int year, int month);
    OrderStats getOrderedProductsStatsByYear(int year);
    OrderStats getOrderedProductsStatsByYearAndCategory(int year);
}
