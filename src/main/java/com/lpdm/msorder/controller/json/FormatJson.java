package com.lpdm.msorder.controller.json;

import com.lpdm.msorder.model.*;
import com.lpdm.msorder.service.ProxyService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FormatJson {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ProxyService proxyService;

    @Autowired
    public FormatJson(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Format the {@link Order} with correct Json arrays
     * @param order The {@link Order} object to format
     * @return The {@link Order} object formated
     */
    public Order formatOrder(Order order){

        Optional<Store> optionalStore = proxyService.findStoreById(order.getStoreId());
        order.setStore(optionalStore.orElse(new Store(order.getStoreId())));

        Optional<User> optionalUser = proxyService.findUserById(order.getCustomerId());
        order.setCustomer(optionalUser.orElse(new User(order.getCustomerId())));

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){
            int productId = orderedProduct.getProductId();
            try{
                Product product = proxyService.findProductById(productId);
                if(product != null) orderedProduct.setProduct(product);
                else orderedProduct.setProduct(new Product(productId, orderedProduct.getPrice()));
            }
            catch (FeignException e) {
                log.warn(e.getMessage());
            }
        }
        return order;
    }
}
