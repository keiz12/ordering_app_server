package com.example.orderingapp.order;

import com.example.orderingapp.dto.order.CreateOrderRequest;
import com.example.orderingapp.dto.order.OrderResponseDTO;
import com.example.orderingapp.dto.order.OrderedProducts;
import com.example.orderingapp.dto.product.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    //CreateOrderRequest

    public CreateOrderRequest createOrder (CreateOrderRequest createOrderRequest) {

        String sql = "INSERT INTO `order`(uuid, table_number, order_paid) VALUES (?, ?, 0)";

        jdbcTemplate.update(sql, createOrderRequest.getUuid(), createOrderRequest.getTableNumber());

        Long id = findOrderIDByUUID(createOrderRequest.getUuid());
        addOrderedProducts(id, createOrderRequest.getOrderedProducts());
        return createOrderRequest;
    }

    private void addOrderedProducts (Long orderId, List<OrderedProducts> orderedProductsList)
    {
        String sql = "INSERT INTO order_products(order_id, product_id, product_quantity) VALUES(?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        OrderedProducts orderedProduct = orderedProductsList.get(i);
                        ps.setLong(1, orderId); // Assuming getOrderId() exists
                        ps.setLong(2, orderedProduct.getProductDTO().getId());
                        ps.setInt(3, orderedProduct.getProductQuantity());
                    }

                    @Override
                    public int getBatchSize() {
                        return orderedProductsList.size();
                    }
        });
    }

    public void updateOrderTableNumber (
            Long orderId,
            CreateOrderRequest request)
    {
        jdbcTemplate.update("""
            UPDATE `order`
            SET table_number = ?
            WHERE id = ?
            """,
                request.getTableNumber(),
                orderId);
    }

    public void updateOrderProduct (Long orderId, List<OrderedProducts> orderedProducts)
    {
        deleteOrderProducts(orderId);
        addOrderedProducts(orderId, orderedProducts);
    }


    public Long findOrderIDByUUID (String uuid) {
        String sql =
                "SELECT id FROM `order` WHERE uuid=?";

        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);

        return jdbcTemplate.queryForObject(sql, mapper, uuid);
    }


    public boolean isOrderPaid(Long orderId) {

        String sql = """
        SELECT order_paid
        FROM `order`
        WHERE id = ?
        """;

        Integer paid = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                orderId);

        return paid != null && paid == 1;
    }

    public CreateOrderRequest getOrderRequestById (Long id) {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setTableNumber(findOrderTable(id));
        createOrderRequest.setOrderedProducts(findOrderedProductsByOrderId(id));
        return createOrderRequest;
    }

    public int findOrderTable (Long id) {
        String sql =
                "SELECT table_number FROM `order` WHERE id=?";

        RowMapper<Integer> mapper = (rs, rn) -> rs.getInt(1);

        return jdbcTemplate.queryForObject(sql, mapper, id);
    }

    public List<OrderedProducts> findOrderedProductsByOrderId(Long orderId) {

        String sql = """
        SELECT
            op.product_quantity,
            p.id,
            p.price
        FROM order_products op
        JOIN product p
            ON op.product_id = p.id
        WHERE op.order_id = ?
        """;

        return jdbcTemplate.query(
                sql,
                rs -> {

                    List<OrderedProducts> orderedProducts = new ArrayList<>();

                    while (rs.next()) {

                        ProductDTO productDTO = new ProductDTO();
                        productDTO.setId(rs.getLong("id"));
                        productDTO.setPrice(rs.getBigDecimal("price"));

                        OrderedProducts orderedProduct =
                                new OrderedProducts();

                        orderedProduct.setProductDTO(productDTO);
                        orderedProduct.setProductQuantity(
                                rs.getInt("product_quantity"));

                        orderedProducts.add(orderedProduct);
                    }

                    return orderedProducts;
                },
                orderId
        );
    }

    public int deleteOrderProducts(Long orderId) {

        String sql = """
        DELETE FROM order_products
        WHERE order_id = ?
        """;

        return jdbcTemplate.update(sql, orderId);
    }

    public int deleteOrder(Long orderId) {

        String sql = """
        DELETE FROM `order`
        WHERE id = ?
        """;

        return jdbcTemplate.update(sql, orderId);
    }

    public List<OrderResponseDTO> findOrdersByOrderID (Long orderID) {

        String sql = """
                SELECT
                    id,
                    uuid,
                    table_number,
                    order_paid,
                    created_at
                FROM `order`
                WHERE id = ?
                ORDER BY created_at DESC
                """;

        List<OrderResponseDTO> orders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Long orderId = rs.getLong("id");

                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setId(orderId);
                    dto.setUuid(rs.getString("uuid"));
                    dto.setTableNumber(rs.getInt("table_number"));
                    dto.setOrderPaid(rs.getBoolean("order_paid"));
                    dto.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    dto.setOrderedProducts(findProductsByOrderId(orderId));

                    return dto;
                },
                orderID
        );

        return orders;
    }


    public List<OrderResponseDTO> findOrdersByDate(LocalDate date) {

        String sql = """
                SELECT
                    id,
                    uuid,
                    table_number,
                    order_paid,
                    created_at
                FROM `order`
                WHERE DATE(created_at) = ?
                ORDER BY created_at DESC
                """;

        List<OrderResponseDTO> orders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Long orderId = rs.getLong("id");

                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setId(orderId);
                    dto.setUuid(rs.getString("uuid"));
                    dto.setTableNumber(rs.getInt("table_number"));
                    dto.setOrderPaid(rs.getBoolean("order_paid"));
                    dto.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    dto.setOrderedProducts(findProductsByOrderId(orderId));

                    return dto;
                },
                date
        );

        return orders;
    }

    private List<OrderedProducts> findProductsByOrderId(Long orderId) {

        String sql = """
                SELECT
                    p.id,
                    p.name,
                    p.description,
                    p.price,
                    op.product_quantity
                FROM order_products op
                INNER JOIN product p
                    ON p.id = op.product_id
                WHERE op.order_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    ProductDTO product = new ProductDTO();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getBigDecimal("price"));

                    OrderedProducts orderedProduct = new OrderedProducts();
                    orderedProduct.setProductDTO(product);
                    orderedProduct.setProductQuantity(
                            rs.getInt("product_quantity")
                    );

                    return orderedProduct;
                },
                orderId
        );
    }

}
