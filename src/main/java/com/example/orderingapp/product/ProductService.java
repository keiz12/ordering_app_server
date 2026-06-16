package com.example.orderingapp.product;

import com.example.orderingapp.dto.product.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductDTO create(ProductDTO product) {

        Long id = repository.save(product);
        product.setId(id);
        return product;
    }

    public java.util.List<ProductDTO> getAll() {
        return repository.findAll();
    }

    public ProductDTO getProduct(String productName) {
        Long id = repository.findProductIdByName(productName);
        return repository.findById(id);
    }


    @Transactional
    public void update(ProductDTO oldProduct,
                       ProductDTO newProduct) {

        Long id = repository.findProductIdByName(oldProduct.getName());

        if (!Objects.equals(
                oldProduct.getName(),
                newProduct.getName())) {

            repository.updateName(
                    id,
                    newProduct.getName()
            );
        }

        if (!Objects.equals(
                oldProduct.getDescription(),
                newProduct.getDescription())) {

            repository.updateDescription(
                    id,
                    newProduct.getDescription()
            );
        }

        if (!Objects.equals(
                oldProduct.getPrice(),
                newProduct.getPrice())) {

            repository.updatePrice(
                    id,
                    newProduct.getPrice()
            );
        }
    }

    public void deleteProductImage (String productName, String productImagePath) {
        repository.deleteImage(repository.findProductIdByName(productName), productImagePath);
    }

    public void insertProductImages (String productName, List<String> productImagePaths) {
        repository.insertImages(repository.findProductIdByName(productName), productImagePaths);
    }

    public void delete(String productName) {
        Long id = repository.findProductIdByName(productName);
        repository.delete(id);
    }
}
