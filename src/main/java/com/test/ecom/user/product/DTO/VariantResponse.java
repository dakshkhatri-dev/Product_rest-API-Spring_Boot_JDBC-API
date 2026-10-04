package com.test.ecom.user.product.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VariantResponse {

    private int inventoryId;
    private String modelNo;
    private String sku;
    private double mrp;
    private double sellingPrice;
    private int stockQuantity;
    private String attributesJson;

}
