package com.test.ecom.user.product;


import com.test.ecom.EComApplication;
import com.test.ecom.user.product.DTO.ParamFilters;
import com.test.ecom.user.product.DTO.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/products")
public class ProductController {


    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);

    private ProductRepository repo ;
    public ProductController (ProductRepository repo){
        this.repo=repo;
    }

    @GetMapping()
    private ResponseEntity<List<ProductResponse>> getPrd(ParamFilters filters){

        //param Filters filters =  new param Filters () ;
        //filters =  new param filters () ;


           log.info("|| Product Request Arrived || ");

        List<ProductResponse> products = repo.getProducts(filters);

          log.info(" Product response generated ");


        // product response products = new product response () ;
        // repo.get products() returns ---  product list --


        return  ResponseEntity.status(HttpStatus.OK).body(products);
    }

}
