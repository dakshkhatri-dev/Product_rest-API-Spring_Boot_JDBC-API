package com.test.ecom.user.product.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ParamFilters {
    // brand table based
    private String brandName;
    private String brandSlug;  // code in database //

    //category table based
    private String categoryName;
    private String categorySlug;

    //sub category table based
    private String subCategoryName;
    private String subCategorySlug;

    //product table based
    private String title;
    private String slug;

    // variants based model no
    private String modelNo;
    // Attributes Next Time //

    // inventory table selling price based .
    @PositiveOrZero(message = "Minimum Price Cannot BE Negative ")
    private BigDecimal minPrice ;
    @PositiveOrZero(message = "Maximum Price Cannot BE Negative ")
    private BigDecimal maxPrice;
    @PositiveOrZero(message = "Minimum Discount Cannot BE Negative ")
    private BigDecimal minDiscount;
    @PositiveOrZero(message = "Maximum Discount Cannot BE Negative ")
    private BigDecimal maxDiscount;

    // important for pagination
    @Min(value = 1,message = "page number must be At least @ 1")
    private int page=1 ;   // default 1
    @Min(value = 1,message = "limit Must be At least @ 1 ")
    @Max(value = 100 , message = "Limit cannot exceed  @ 100")
    private int limit = 20 ;

    // order by
    private SortOrder orderBy=SortOrder.ASC;  // need enums ascending or descending
    private SortBy sortBy = SortBy.PRICE; // need enums discount or price

    //IN Stock
       private Boolean inStock;  // NULL or TRUE



}
