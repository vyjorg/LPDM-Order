package com.lpdm.msorder.controller;

import com.lpdm.msorder.exception.*;
import com.lpdm.msorder.model.order.*;
import com.lpdm.msorder.model.product.Product;
import com.lpdm.msorder.model.user.*;
import com.lpdm.msorder.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Stream;

import static com.lpdm.msorder.utils.ValueType.*;

/**
 * @author Kybox
 * @version 1.0
 * @since 01/12/2018
 */

@RestController
@RequestMapping(ADMIN_PATH)
@Api(tags = {"Admin Rest API"})
public class AdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceService invoiceService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final CouponService couponService;
    private final OrderedProductService orderedProductService;

    @Autowired
    public AdminController(OrderService orderService,
                           InvoiceService invoiceService,
                           PaymentService paymentService,
                           DeliveryService deliveryService,
                           CouponService couponService,
                           OrderedProductService orderedProductService) {

        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.couponService = couponService;
        this.orderedProductService = orderedProductService;
    }

    /**
     * Add a new {@link Payment}
     * @param payment The new {@link Payment} object
     * @return The new {@link Payment} added
     */
    @ApiOperation(
            value = "Add a new payment method",
            notes = "Please note that the addition of a new payment method " +
                    "must also include a new payment API.")
    @PutMapping(value = "/payment/add", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Payment addNewPayment(@Valid @RequestBody Payment payment) {

        try { payment = paymentService.savePayment(payment); }
        catch (Exception e) { throw new PaymentPersistenceException(); }
        return payment;
    }

    /**
     * Delete a {@link Payment} object
     * @param payment The valid {@link Payment} object to delete
     * @return If it succeeded or not otherwise throw an exception
     */
    @ApiOperation(
            value = "Delete a payment",
            notes = "Please note that the removal of payment method " +
                    "does not involve the suppression of the payment API."
    )
    @DeleteMapping(value = "/payment/delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean deletePayment(@Valid @RequestBody Payment payment) {

        try { paymentService.deletePayment(payment); }
        catch (Exception e) { throw new DeleteEntityException(); }
        return !paymentService.checkIfPaymentExist(payment.getId());
    }

    /**
     * Delete a {@link Order} object
     * @param order The valid {@link Order} object to delete
     * @return If it succeeded or not otherwise throw an exception
     */
    @ApiOperation(
            value = "Delete an order",
            notes = "Deleting an order should not be allowed except possibly if it is not paid.")
    @DeleteMapping(value = "/order/delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean deleteOrder(@Valid @RequestBody Order order){

        if(order.getStatus().getId() >= Status.PAID.getId())
            throw new BadRequestException();

        try { orderService.deleteOrder(order); }
        catch (Exception e) { throw new DeleteEntityException(); }
        return !orderService.checkIfOrderExist(order.getId());
    }

    /**
     * Get all {@link Order} objects sorted by ASC or DESC
     * @param sort The sort direction
     * @param size The maximum {@link Order} by pages
     * @param page The page number
     * @return The {@link List<Order>} sorted
     */
    @ApiOperation(
            value = "Finds all orders sorted by date",
            notes = "The result of the query can be paginated by populating the size and page attributes.")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping(value = "/orders/all/date/{sort}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllSortedByDate(@PathVariable String sort,
                                           @RequestParam Optional<Integer> size,
                                           @RequestParam Optional<Integer> page){
        Sort sortDate;
        switch (sort.toLowerCase()){
            case SORTED_BY_ASC:
                sortDate = new Sort(Sort.Direction.ASC, "orderDate");
                break;
            case SORTED_BY_DESC:
                sortDate = new Sort(Sort.Direction.DESC, "orderDate");
                break;
            default:
                throw new BadRequestException();
        }

        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(Integer.MAX_VALUE), sortDate);

        return orderService.findAllOrdersPageable(pageRequest);
    }

    /**
     * Find {@link Order} by {@link Product} id
     * @param id The product id
     * @return The order {@link List}
     */
    @ApiOperation(
            value = "Find all orders based on the product id",
            notes = "The result of the query can be consequent, " +
                    "it would be necessary to add a pagination option on the result.")
    @GetMapping(value = "/orders/all/product/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByProductId(@PathVariable int id){

        List<OrderedProduct> orderedProductList = orderedProductService.findAllOrderedProductsByProductId(id);
        List<Order> orderList = new ArrayList<>();
        orderedProductList.forEach(o -> orderList.add(orderService.findOrderById(o.getOrder().getId())));
        return orderList;
    }

    /**
     * Find {@link Order} by {@link Payment} id
     * @param id Payment id
     * @return The order {@link List}
     */
    @ApiOperation(
            value = "Find all orders based on the payment id",
            notes = "The result of the query can be consequent, " +
                    "it would be necessary to add a pagination option on the result.")
    @GetMapping(value = "/orders/all/payment/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByPaymentId(@PathVariable int id){

        Payment payment = paymentService.findPaymentById(id);
        return orderService.findAllOrdersByPayment(payment);
    }

    /**
     * Finds all {@link Order} with the {@link Status} defined in the id parameter
     * @param id The status id
     * @param page The optional page number
     * @param size The optional page size
     * @return A {@link List<Order>} object containing orders found
     */
    @ApiOperation(
            value = "Find all orders based on the status id",
            notes = "The result of the query can be consequent, " +
                    "so you can set optionals pagination data"
    )
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping(value = "/orders/all/status/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByStatus(@PathVariable int id,
                                       @RequestParam(required = false) Optional<Integer> page,
                                       @RequestParam(required = false) Optional<Integer> size){

        Optional<Status> status = Stream.of(Status.values()).filter(s -> s.getId() == id).findFirst();
        if(status.isPresent()){
            PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(Integer.MAX_VALUE));
            return orderService.findAllOrdersByStatusPageable(status.get(), pageRequest);
        }
        else throw new OrderNotFoundException();
    }

    /**
     * Find an {@link Order} based on its {@link Invoice} reference
     * @param ref The {@link Invoice} reference
     * @return The {@link Order} found
     */
    @ApiOperation(
            value = "Find an order based on its invoice reference",
            notes = "Be careful, an order does not always have an invoice"
    )
    @GetMapping(value = "/orders/invoice/{ref}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Order findByInvoiceRef(@PathVariable String ref){

        Invoice invoice = invoiceService.findInvoiceByReference(ref);

        return orderService.findOrderById(invoice.getOrderId());
    }

    /**
     * Find all the customer's order by his e-mail address
     * @param email The {@link User} email address
     * @return The {@link List<Order>} object which contains the orders found
     */
    @ApiOperation(
            value = "Find all the customer's order by his e-mail address",
            notes = "The result of the query can be consequent, " +
                    "it would be necessary to add a pagination option on the result."
    )
    @GetMapping(value = "/orders/all/customer/email/{email}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByEmail(@PathVariable String email){

        return orderService.findAllOrdersByCustomer(EMAIL, email);
    }

    /**
     * Find all the customer's order by his name
     * @param name The {@link User} name
     * @return The {@link List<Order>} object which contains the orders found
     */
    @ApiOperation(
            value = "Find all the customer's order by his name",
            notes = "The result of the query can be consequent, " +
                    "it would be necessary to add a pagination option on the result."
    )
    @GetMapping(value = "orders/all/customer/name/{name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByName(@PathVariable String name){

        return orderService.findAllOrdersByCustomer(NAME, name);
    }

    /**
     * Find all {@link Order} objects between 2 dates
     * @param searchDates The {@link SearchDates} object that contains 2 dates
     * @return The {@link Order} {@link List} found
     */
    @ApiOperation(value = "Find all orders between 2 dates")
    @PostMapping(value = "/orders/dates/between",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Order> findAllByDateBetween(@Valid @RequestBody SearchDates searchDates){

        return orderService.findAllOrdersBetweenTwoDates(searchDates);
    }

    /**
     * Persist a new {@link Delivery} object
     * @param delivery The {@link Delivery} object to persist
     * @return the {@link Delivery} object that has been persisted
     */
    @ApiOperation(value = "Persist a new delivery object")
    @PostMapping(value = "/delivery/add",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Delivery addNewDeliveryMethod(@Valid @RequestBody Delivery delivery){

        return deliveryService.addNewDeliveryMethod(delivery);
    }

    /**
     * Update a {@link Delivery} object in the datebase
     * @param delivery The {@link Delivery} object to update
     * @return The {@link Delivery} object updated
     * @throws DeliveryNotFoundException Thrown if no {@link Delivery} object was found
     */
    @ApiOperation(value = "Update an existing delivery object")
    @PutMapping(value = "/delivery/update",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Delivery updateDelivery(@Valid @RequestBody Delivery delivery)
            throws DeliveryNotFoundException {

        return deliveryService.updateDeliveryMethod(delivery);
    }

    /**
     * Delete the {@link Delivery} object in the database
     * @param delivery The {@link Delivery} object to delete
     * @return True if the {@link Delivery} object was deleted, otherwise false
     * @throws DeliveryNotFoundException Thrown if the {@link Delivery} object was found in the database
     */
    @ApiOperation(value = "Delete the delivery object in the database")
    @DeleteMapping(value = "/delivery/delete",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean deleteDeliveryMethod(@Valid @RequestBody Delivery delivery)
            throws DeliveryNotFoundException{

        return deliveryService.deleteDeliveryMethod(delivery);
    }

    /**
     * Find all {@link Coupon} codes in the database
     * @return A {@link Coupon} {@link List}
     * @throws CouponNotFoundException Thrown if no coupon was found
     */
    @ApiOperation(value = "Find all coupon codes in the database")
    @GetMapping(value = "/coupon/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Coupon> getCouponList() throws CouponNotFoundException {

        return couponService.findAllCoupons();
    }

    /**
     * Adding a new {@link Coupon}
     * @param coupon The new {@link Coupon} object to add
     * @return The added {@link Coupon}
     */
    @ApiOperation(value = "Adding a new coupon")
    @PostMapping(value = "/coupon/add",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Coupon addNewCoupon(@RequestBody Coupon coupon){

        return couponService.addNewCoupon(coupon);
    }

    /**
     * Delete a {@link Coupon}
     * @param coupon The {@link Coupon} object to be deleted
     * @return True if the {@link Coupon} has been deleted, otherwise false
     */
    @ApiOperation(value = "Delete a coupon")
    @DeleteMapping(value = "/coupon/delete",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean deleteCoupon(@RequestBody Coupon coupon){

        return couponService.deleteCoupon(coupon);
    }

    /**
     * Update an existing {@link Coupon} object
     * @param coupon The {@link Coupon} object to update
     * @return The {@link Coupon} object updated
     * @throws CouponNotFoundException Thrown if no {@link Coupon} was found in the database
     */
    @ApiOperation(value = "Update a coupon")
    @PutMapping(value = "/coupon/update",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Coupon updateCoupon(@RequestBody Coupon coupon) throws CouponNotFoundException {

        return couponService.updateCoupon(coupon);
    }
}
