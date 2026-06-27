package com.example.orderingapp.product;

import com.example.orderingapp.dto.product.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long save(ProductDTO product) {

        String sql =
                "INSERT INTO product(name, description, price) VALUES(?,?,?)";

        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice());

        Long productId = findProductIdByName(product.getName());

        insertImages(productId, product.getImagePathToDeletePath());

        return productId;
    }

    public void insertImages(Long productId,  HashMap<String, String> images) {

        if (images == null || images.isEmpty()) {
            return;
        }

        // image_delete_path

        // image_path

        String sql =
                "INSERT INTO product_image(product_id,image_path, image_delete_path) VALUES(?,?,?)";

        for (Map.Entry<String, String> set : images.entrySet())
            jdbcTemplate.update(sql, productId, set.getKey(), set.getValue());
    }

    public ProductDTO findById (Long id) {

        String sql =
                "SELECT * FROM product WHERE id=?";

        List<ProductDTO> products =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            ProductDTO dto = new ProductDTO();
                            dto.setId(rs.getLong("id"));
                            dto.setName(rs.getString("name"));
                            dto.setDescription(rs.getString("description"));
                            dto.setPrice(rs.getBigDecimal("price"));
                            return dto;
                        },
                        id
                );

        if (!products.isEmpty())
            products.getFirst().setImageURLPath(findImagesByProductId(id));

        return products.isEmpty() ? null : products.getFirst();
    }

    public String findProductNameByID (long id) {

        String sql = "SELECT name FROM product WHERE id=?";

        RowMapper<String> mapper = (rs, rn) -> rs.getString(1);

        var l = jdbcTemplate.query(sql,mapper,id);

        return l.isEmpty() ? null : l.getFirst();
    }


    public Long findProductIdByName(String productName) {

        String sql =
                "SELECT id FROM product WHERE name=?";

        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);

        return jdbcTemplate.queryForObject(sql, mapper, productName);
    }


    public List<String> findImagesByProductId(Long productId) {

        String sql =
                "SELECT image_path FROM product_image WHERE product_id=?";

        return jdbcTemplate.queryForList(
                sql,
                String.class,
                productId
        );
    }


    public List<ProductDTO> findAll() {

        String sql = "SELECT * FROM product";

        List<ProductDTO> products =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            ProductDTO dto = new ProductDTO();

                            dto.setId(rs.getLong("id"));
                            dto.setName(rs.getString("name"));
                            dto.setDescription(rs.getString("description"));
                            dto.setPrice(rs.getBigDecimal("price"));

                            return dto;
                        });

        products.forEach(p ->
                p.setImageURLPath(findImagesByProductId(p.getId()))
        );

        return products;
    }

    public void updateName(Long id, String name) {

        jdbcTemplate.update(
                "UPDATE product SET name=? WHERE id=?",
                name,
                id
        );
    }

    public void updateDescription(Long id, String description) {

        jdbcTemplate.update(
                "UPDATE product SET description=? WHERE id=?",
                description,
                id
        );
    }

    public void updatePrice(Long id, java.math.BigDecimal price) {

        jdbcTemplate.update(
                "UPDATE product SET price=? WHERE id=?",
                price,
                id
        );
    }

    public void deleteImage(Long productId, List<String> toDeleteImagesPath) {


        for (String path : toDeleteImagesPath)
        {
            jdbcTemplate.update(
                    "DELETE FROM product_image WHERE product_id=? AND image_path=?",
                    productId,
                    path);
        }
    }

    public void deleteImages(Long productId) {

        jdbcTemplate.update(
                "DELETE FROM product_image WHERE product_id=?",
                productId
        );
    }

    public void delete(Long id) {

        jdbcTemplate.update(
                "DELETE FROM product WHERE id=?",
                id
        );
    }

}
