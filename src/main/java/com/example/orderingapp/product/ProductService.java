package com.example.orderingapp.product;

import com.example.orderingapp.dto.product.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public void update(String oldProductName,
                       ProductDTO newProduct) {

        Long id = repository.findProductIdByName(oldProductName);

        ProductDTO oldProduct = repository.findById(id);

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
                newProduct.getPrice()))
        {

            repository.updatePrice(
                    id,
                    newProduct.getPrice()
            );
        }

        updateImages(id, newProduct.getImagePathToDeletePath(), newProduct.getImageURLPath());
    }

    private void updateImages
            (Long productID, HashMap<String, String> newImagePathToDeletePath, List<String> toDeleteImagesPath)
    {
        repository.deleteImage(productID, toDeleteImagesPath);
        repository.insertImages(productID, newImagePathToDeletePath);
    }

    public void delete(String productName) {
        Long id = repository.findProductIdByName(productName);
        ProductDTO productDTO = repository.findById(id);
        repository.deleteImage(id, productDTO.getImageURLPath());
        repository.delete(id);
    }
}
