ALTER TABLE product CHANGE column name name VARCHAR(200) NOT NULL UNIQUE;

ALTER TABLE product_image CHANGE column image_path image_path VARCHAR(500) NOT NULL UNIQUE;