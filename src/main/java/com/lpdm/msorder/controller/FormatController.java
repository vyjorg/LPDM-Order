package com.lpdm.msorder.controller;

import com.lpdm.msorder.model.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

/**
 * @author Kybox
 * @version 1.0
 * @since 01/12/2018
 */

@Controller
public class FormatController {

    private final Logger log = LogManager.getLogger(this.getClass());

    /**
     * Autowired - Controller
     */
    @Autowired
    private UserController userController;

    @Autowired
    private StoreController storeController;

    @Autowired
    private ProductController productController;

    /**
     * Default constructor
     */
    public FormatController(){

    }

    /**
     * Format the {@link Order} with correct Json arrays
     * @param order The {@link Order} object to format
     * @return The {@link Order} object formated
     */
    public Order formatOrder(Order order){

        Optional<Store> optionalStore = storeController.findStoreById(order.getStoreId());
        order.setStore(optionalStore.orElse(new Store(order.getStoreId())));

        Optional<User> optionalUser = userController.findUserById(order.getCustomerId());
        order.setCustomer(optionalUser.orElse(new User(order.getCustomerId())));

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            int productId = orderedProduct.getOrderedProductPK().getProductId();
            Optional<Product> optionalProduct = productController.findProductById(productId);
            orderedProduct.setProduct(optionalProduct.orElse(new Product(productId)));
        }

        return order;
    }
}