package com.test.ecom.user.product.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {


    private int productId;
    private String brandName;
    //  private String brandSlug;  // code in database //

    //category table based
    private String categoryName;
    //  private String categorySlug;

    //sub category table based
    private String subCategoryName;
    //  private String subCategorySlug;

    //product table based
    private String title;
    private String description;

    // variants
    private List<VariantResponse> vr = new ArrayList<>();

}