package com.example.orderingapp.product;

import com.example.orderingapp.dto.product.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long save(ProductDTO product) {

        String sql =
                "INSERT INTO product(name, description, price) VALUES(?,?,?)";

        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice());

        Long productId = findProductIdByName(product.getName());

        insertImages(productId, product.getImagePaths());

        return productId;
    }

    public void insertImages(Long productId, List<String> images) {

        if (images == null || images.isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO product_image(product_id,image_path) VALUES(?,?)";

        jdbcTemplate.batchUpdate(
                sql,
                images,
                images.size(),
                (ps, image) -> {
                    ps.setLong(1, productId);
                    ps.setString(2, image);
                }
        );
    }

    public ProductDTO findById (Long id) {

        String sql =
                "SELECT * FROM product WHERE id=?";

        ProductDTO product =
                jdbcTemplate.queryForObject(
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

        product.setImagePaths(findImagesByProductId(id));

        return product;
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
                p.setImagePaths(findImagesByProductId(p.getId()))
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

    public void deleteImage(Long productId, String imagePath) {

        jdbcTemplate.update(
                "DELETE FROM product_image WHERE product_id=? AND image_path=?",
                productId,
                imagePath
        );
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
