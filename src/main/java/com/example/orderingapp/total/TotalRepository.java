package com.example.orderingapp.total;


import lombok.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

@RequiredArgsConstructor
@Repository
public class TotalRepository {

    private final JdbcTemplate jdbcTemplate;

    public int getTotalStaff () {

        String sql =
                """
                    SELECT COUNT(u.id) FROM user u
                    JOIN authorization_roles ar ON ar.user_id = u.id
                    WHERE ar.role = 'STAFF'
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalOrderedProducts () {

        String sql =
                """
                    SELECT DISTINCT COUNT(DISTINCT op.product_id)
                    FROM order_products op
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalOrderPrice () {

        String sql =
                """
                    SELECT SUM(op.product_quantity*p.price)
                    FROM product p
                    JOIN order_products op ON op.product_id = p.id
                    JOIN `order` o ON o.id = op.order_id
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
