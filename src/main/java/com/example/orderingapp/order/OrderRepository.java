package com.example.orderingapp.order;

import com.example.orderingapp.dto.order.*;
import com.example.orderingapp.dto.product.ProductDTO;
import com.example.orderingapp.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public Order createOrder (Order order, ProductRepository productRepository) {

        String sql = "INSERT INTO `order`(uuid, table_number, order_paid) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, order.getUuid(), order.getTableNumber(), order.isPaid());

        Long id = findOrderIDByUUID(order.getUuid());

        List<String> toRemoveOrderedProducts = addOrderedProducts(id, order.getProductNameToQty(), productRepository);

        removeOrderedProducts(toRemoveOrderedProducts, order.getProductNameToQty());

        return order;
    }

    private List<String> addOrderedProducts (Long orderId, HashMap<String, Integer> productNameToQty, ProductRepository productRepository)
    {
        String sql = "INSERT INTO order_products(order_id, product_id, product_quantity) VALUES(?, ?, ?)";

        List<String> toRemoveOrderedProducts = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : productNameToQty.entrySet())
        {
            String productName = entry.getKey();
            int productQty = entry.getValue();

            Long productID = productRepository.findProductIdByName(productName);

            if (productID.equals(null)) {
                toRemoveOrderedProducts.add(productName);
                continue;
            }

            jdbcTemplate.update(sql, orderId, productID, productQty);
        }
        return toRemoveOrderedProducts;
    }

    private void removeOrderedProducts (List<String> toRemoveOrderedProducts, HashMap<String, Integer> productNameToQty)
    {
        toRemoveOrderedProducts.forEach(e -> {
            productNameToQty.remove(e);
        });
    }

    public boolean isOrderPaid (String orderUuid)
    {
        String sql = "SELECT order_paid FROM `order` WHERE uuid=?";

        RowMapper<Boolean> mapper = (rs, rn) -> rs.getBoolean(1);

        List<Boolean> l = jdbcTemplate.query(sql, mapper, orderUuid);

        return l.isEmpty() ? false : l.getFirst();
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
//        deleteOrderProducts(orderId);
//        addOrderedProducts(orderId, orderedProducts);
    }


    public Long findOrderIDByUUID (String uuid) {
        String sql =
                "SELECT id FROM `order` WHERE uuid=?";

        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);

        var l = jdbcTemplate.query(sql, mapper, uuid);

        return l.isEmpty() ? null : l.getFirst();
    }

    public Order findOrderByUUID (String uuid, ProductRepository productRepository) {

        String sql = "SELECT id FROM `order` WHERE uuid=?";

        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);

        var l = jdbcTemplate.query(sql, mapper, uuid);

        Long id =  l.isEmpty() ? null : l.getFirst();

        if (id != null)
            return getOrder(id, productRepository);

        return null;
    }

    public Order getOrder (long id, ProductRepository productRepository) {

        String sql = "SELECT * FROM `order` WHERE id=?";

        RowMapper<Order> mapper = (rs, rn) -> {

            Order order = new Order();

            order.setId(rs.getLong(1));
            order.setUuid(rs.getString(2));
            order.setTableNumber(rs.getInt(3));
            order.setPaid(rs.getBoolean(4));
            order.setCreatedAt(rs.getObject(5, LocalDateTime.class));

            return order;
        };

        Order order = jdbcTemplate.queryForObject(sql, mapper, id);

        populateOrderProductNameToQty_ToPriceMap(order, productRepository);

        return order;
    }

    public void populateOrderProductNameToQty_ToPriceMap (Order order, ProductRepository productRepository, long... id)
    {
        long orderID = (id.length == 0) ? order.getId() : id[0];

        List<Long> orderProductIDs = getOrderProductIDs(orderID);

        List<ProductDTO> productDTOs = getOrderProductDTOs(productRepository, orderProductIDs);

        setOrderProductNameToPriceMap(order, productDTOs);

        setOrderProductNameToQtyMap(order, productDTOs);
    }

    public List<ProductDTO> getOrderProductDTOs (ProductRepository productRepository, List<Long> orderProductIDs) {

        List<ProductDTO> productDTOs = new ArrayList<>();

        orderProductIDs.forEach( e ->
        {
            ProductDTO productDTO = productRepository.findById(e);

            if (productDTO != null)
                productDTOs.add(productDTO);
        });

        return productDTOs;
    }

    public void setOrderProductNameToQtyMap (Order order, List<ProductDTO> productDTOs)
    {
        String sql = "SELECT order_products.product_quantity FROM order_products WHERE order_products.order_id=? AND order_products.product_id=?";

        RowMapper<Integer> mapper = (rs, rn) -> rs.getInt(1);

        productDTOs.forEach(e ->
        {
            var l = jdbcTemplate.query(sql, mapper, order.getId(), e.getId());

            if (!l.isEmpty())
                order.getProductNameToQty().put(e.getName(), l.getFirst());
        });
    }

    public void setOrderProductNameToPriceMap (Order order, List<ProductDTO> productDTOs)
    {
        productDTOs.forEach(e -> {
            order.getProductNameToPrice().put(e.getName(), e.getPrice());
        });
    }

    public List<Long> getOrderProductIDs (long orderID)
    {
        String sql = "SELECT product_id FROM order_products WHERE order_id=?";

        RowMapper<Long> mapper = (rs,rn) -> rs.getLong(1);

        return jdbcTemplate.query(sql, mapper, orderID);
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
