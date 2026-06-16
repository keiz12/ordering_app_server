package com.example.orderingapp.product;

import com.example.orderingapp.dto.product.ProductDTO;
import com.example.orderingapp.dto.product.ProductImageDelete;
import com.example.orderingapp.dto.product.ProductImageInsert;
import com.example.orderingapp.dto.product.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController
{
    private final ProductService service;

    @PostMapping("/secure/product")
    public ResponseEntity<?> create(
            @RequestBody ProductDTO product) {

        return ResponseEntity.ok(
                service.create(product)
        );
    }

    @GetMapping("/all/product/{productName}")
    public ResponseEntity<?> getProduct(
            @PathVariable String productName) {

        return ResponseEntity.ok(
                service.getProduct(productName)
        );
    }

    @PostMapping("/secure/product/insert/images")
    public void insertProductImages (@RequestBody ProductImageInsert productImageInsert) {
        service.insertProductImages(productImageInsert.getProductName(), productImageInsert.getProductImagePaths());
    }

    @GetMapping("/all/product")
    public ResponseEntity<List<ProductDTO>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    @PutMapping("/secure/product")
    public ResponseEntity<String> update(
            @RequestBody ProductUpdateRequest request) {

        service.update(
                request.getOldProduct(),
                request.getNewProduct()
        );

        return ResponseEntity.ok(
                "Product updated successfully"
        );
    }

    @DeleteMapping("/secure/product/{productName}")
    public ResponseEntity<String> delete(
            @PathVariable String productName) {

        service.delete(productName);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }

    @DeleteMapping("/secure/product/delete/images")
    public void deleteProductImage (@RequestBody ProductImageDelete productImageDelete) {
        service.deleteProductImage(productImageDelete.getProductName(), productImageDelete.getProductImagePath());
    }

    @GetMapping("/secure/product/test")
    public String test () {
        return "Test";
    }

}
