use ordering_app;

SELECT * FROM `order`
JOIN order_products ON order_products.order_id = `order`.id;

SELECT order_paid FROM `order` WHERE uuid='123';

SELECT * FROM `order` WHERE DATE(created_at) = STR_TO_DATE('20-06-2026', '%d-%m-%Y');

-- STR_TO_DATE('15-05-2024', '%d-%m-%Y')

SELECT created_at FROM `order` order by created_at;

SELECT o.uuid, order_paid, s.processed_at
FROM `order` o 
LEFT JOIN staff_processed_order s ON s.order_id = o.id
WHERE DATE(created_at) = STR_TO_DATE('20-06-2026', '%d-%m-%Y');


-- REVENUE STATISTICS

SELECT DATE_FORMAT(o.created_at, '%d-%m-%Y'), SUM(op.product_quantity * p.price)
FROM `order` o
JOIN order_products op ON op.order_id = o.id
JOIN product p ON p.id = op.product_id
-- WHERE DATE(created_at) >= STR_TO_DATE('20-06-2026', '%d-%m-%Y') AND DATE(created_at) <= STR_TO_DATE('23-06-2026', '%d-%m-%Y')
GROUP BY (DATE_FORMAT(o.created_at, '%d-%m-%Y'));

SELECT o.created_at, op.product_quantity, p.price
FROM `order` o
JOIN order_products op ON op.order_id = o.id
JOIN product p ON p.id = op.product_id 
ORDER BY DATE(o.created_at);


-- Products STATISTICS

SELECT p.name, SUM(op.product_quantity)
FROM product p
JOIN order_products op ON op.product_id = p.id
JOIN `order` o ON o.id = op.order_id
WHERE DATE(created_at) >= STR_TO_DATE('20-06-2026', '%d-%m-%Y') AND DATE(created_at) <= STR_TO_DATE('20-06-2026', '%d-%m-%Y')
GROUP BY (p.id);

SELECT * FROM order_products;

-- Staff STATISTICS

SELECT u.username, COUNT(spo.staff_id)
FROM staff_processed_order spo
JOIN user u ON u.id = spo.staff_id
GROUP BY(spo.staff_id);

SELECT *
FROM staff_processed_order;


