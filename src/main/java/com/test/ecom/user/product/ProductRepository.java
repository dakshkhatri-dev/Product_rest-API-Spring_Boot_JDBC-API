package com.test.ecom.user.product;



import com.test.ecom.user.product.DTO.ParamFilters;
import com.test.ecom.user.product.DTO.ProductResponse;
import com.test.ecom.user.product.DTO.VariantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepository {


    private static final Logger log =
            LoggerFactory.getLogger(ProductRepository.class);


        private final NamedParameterJdbcTemplate jdbcTemplate ;

         public ProductRepository(NamedParameterJdbcTemplate jdbcTemplate){
             this.jdbcTemplate=jdbcTemplate;
         }
         public List<ProductResponse> getProducts(ParamFilters filters){
                                 log.info("Product Repository Active");
             //param Filters filters =  new param Filters () ;
             //filters =  new param filters () ;

             String sql = """
                     SELECT 
                     p.id AS product_id ,
                     p.title  ,
                     p.description,
                     
                     b.name AS brand_name,
                     
                     c.name AS category_name ,
                     
                     sc.name AS sub_category_name ,
                     
                     pv.mpn_model_no AS model_no ,
                     pv.attributes::text AS attributes_json ,
                     
                     i.id AS inventory_id ,
                     i.sku ,
                     i.mrp,
                     i.selling_price,
                     i.stock_quantity 
                     FROM products p 
                     JOIN brands b ON p.brand_id = b.id
                     JOIN categories c ON p.category_id = c.id 
                     JOIN sub_categories sc ON p.sub_category_id = sc.id
                     JOIN product_variants pv ON pv.product_id = p.id 
                     JOIN inventory i ON i.variant_id = pv.id 
                     WHERE 1=1 
                     AND (cast(:brandSlug AS TEXT) IS NULL OR :brandSlug = '' OR b.code=:brandSlug)
                     AND (cast(:brandName AS TEXT) IS NULL OR :brandName= '' OR b.name=:brandName)
                     
                        AND (cast(:categorySlug AS TEXT) IS NULL OR :categorySlug = '' OR c.slug=:categorySlug)
                        AND (cast(:categoryName AS TEXT) IS NULL OR :categoryName = '' OR c.name=:categoryName)
                        
                         AND (cast(:subCategorySlug AS TEXT) IS NULL OR :subCategorySlug = '' OR sc.slug=:subCategorySlug)
                        AND (cast(:subCategoryName AS TEXT) IS NULL OR :subCategoryName = '' OR sc.name=:subCategoryName) 
                        
                       AND (cast(:slug AS TEXT) IS NULL OR :slug = '' OR p.slug=:slug)
                      AND (cast(:title AS TEXT) IS NULL OR :title = '' OR p.title ILIKE '%' || :title || '%')
                        
                         AND (cast(:modelNo AS TEXT)IS NULL OR :modelNo= '' OR pv.mpn_model_no=:modelNo)
                         
                         AND (cast(:minPrice AS NUMERIC) IS NULL OR  i.selling_price>=:minPrice)
                          AND (cast(:maxPrice AS NUMERIC)IS NULL  OR i.selling_price<=:maxPrice)
                          
                         AND (cast(:minDiscount AS NUMERIC) IS NULL  OR i.discount>=:minDiscount)  
                          AND (cast(:maxDiscount AS NUMERIC) IS NULL  OR i.discount<=:maxDiscount)
                            
                            AND ( CAST(:inStock AS BOOLEAN) IS NULL OR (:inStock = true AND i.stock_quantity > 0))   
                        
                       ORDER BY
                           CASE
                               WHEN :sortBy = 'PRICE' AND :orderBy = 'ASC'
                               THEN i.selling_price
                           END ASC,
                     
                           CASE
                               WHEN :sortBy = 'PRICE' AND :orderBy = 'DESC'
                               THEN i.selling_price
                           END DESC,
                     
                           CASE
                               WHEN :sortBy = 'DISCOUNT' AND :orderBy = 'ASC'
                               THEN i.discount
                           END ASC,
                     
                           CASE
                               WHEN :sortBy = 'DISCOUNT' AND :orderBy = 'DESC'
                               THEN i.discount
                           END DESC,
                     
                           p.id ASC
                     
                       LIMIT :limit
                       OFFSET (:page - 1) * :limit
                     """ ;
             // Step A: Convert DTO params into SQL parameters
           //  BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(filters) ;
             MapSqlParameterSource params = new MapSqlParameterSource()

                     // Brand
                     .addValue("brandSlug", filters.getBrandSlug())
                     .addValue("brandName", filters.getBrandName())

                     // Category
                     .addValue("categorySlug", filters.getCategorySlug())
                     .addValue("categoryName", filters.getCategoryName())

                     // Sub-category
                     .addValue("subCategorySlug", filters.getSubCategorySlug())
                     .addValue("subCategoryName", filters.getSubCategoryName())

                     // Product
                     .addValue("slug", filters.getSlug())
                     .addValue("title", filters.getTitle())

                     // Variant
                     .addValue("modelNo", filters.getModelNo())

                     // Price
                     .addValue("minPrice", filters.getMinPrice())
                     .addValue("maxPrice", filters.getMaxPrice())

                     // Discount
                     .addValue("minDiscount", filters.getMinDiscount())
                     .addValue("maxDiscount", filters.getMaxDiscount())

                     // Stock
                     .addValue("inStock", filters.getInStock())

                     // Sorting
                     .addValue(
                             "sortBy",
                             filters.getSortBy() != null
                                     ? filters.getSortBy().name()
                                     : "PRICE"
                     )

                     .addValue(
                             "orderBy",
                             filters.getOrderBy() != null
                                     ? filters.getOrderBy().name()
                                     : "ASC"
                     )

                     // Pagination
                     .addValue("page", filters.getPage())
                     .addValue("limit", filters.getLimit());
             // Step B: Use a Map to combine duplicate product rows into a single product with multiple variants
             Map<Integer,ProductResponse> productMap = new LinkedHashMap<>();

             /**
              * JDBC response
              * Use map to store product id and product named product response object
              * search for product id in map if found update variants if not found new product created which is reference of product response
              * whole map named productMap delivered to Client .
              */


             // QUERY begin
             jdbcTemplate.query(sql,params,rs ->{
                    log.info("ROW Fetched");
                      int productId= rs.getInt("product_id");

                 // Check if we already created this product object in our map
                ProductResponse product = productMap.get(productId);

                 // If product isn't in map yet, create it once
               if(product == null ){
                   product = new ProductResponse();
                           product.setProductId(productId);
                     product.setBrandName(rs.getString("brand_name"));
                     product.setCategoryName(rs.getString("category_name"));
                     product.setSubCategoryName(rs.getString("sub_category_name"));
                     product.setTitle(rs.getString("title"));
                     product.setDescription(rs.getString("description"));

                   // Store in map so we can reuse it when next row has same productId
                   productMap.put(productId,product);

               }

               // Create variant object for current row
                 VariantResponse variant = new VariantResponse();
                   variant.setInventoryId(rs.getInt("inventory_id"));
                   variant.setMrp(rs.getDouble("mrp"));
                   variant.setModelNo(rs.getString("model_no"));
                   variant.setSku(rs.getString("sku"));
                   variant.setSellingPrice(rs.getDouble("selling_price"));
                   variant.setStockQuantity(rs.getInt("stock_quantity"));
                   variant.setAttributesJson(rs.getString("attributes_json"));

                 // Add variant to product's variants list
                   product.getVr().add(variant);
             });
             //QUERY end

             log.info("ROWS Fetched Ready To return !");
             // Step C: Return list of unique products containing their nested variants
             return new ArrayList<>(productMap.values());
         }
}
