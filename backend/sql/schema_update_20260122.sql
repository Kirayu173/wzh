-- Align MySQL schema with current JPA entities (order, order_item, trace).

ALTER TABLE `order`
  ADD COLUMN receiver VARCHAR(64) NULL,
  ADD COLUMN phone VARCHAR(20) NULL,
  ADD COLUMN address VARCHAR(255) NULL,
  ADD COLUMN memo VARCHAR(255) NULL,
  ADD COLUMN request_id VARCHAR(64) NULL,
  ADD COLUMN confirm_time DATETIME NULL;

ALTER TABLE `order`
  ADD INDEX idx_order_user (user_id),
  ADD INDEX idx_order_status (status),
  ADD UNIQUE KEY uk_order_request (user_id, request_id);

ALTER TABLE order_item
  ADD COLUMN product_name VARCHAR(128) NULL,
  ADD COLUMN product_image VARCHAR(255) NULL,
  MODIFY quantity INT NOT NULL;

ALTER TABLE trace_batch
  MODIFY product_id BIGINT NOT NULL,
  MODIFY trace_code VARCHAR(64) NOT NULL,
  ADD COLUMN create_time DATETIME NULL,
  ADD UNIQUE KEY uk_trace_code (trace_code),
  ADD INDEX idx_trace_product (product_id);

ALTER TABLE logistics_node
  MODIFY trace_code VARCHAR(64) NOT NULL,
  MODIFY node_time DATETIME NOT NULL,
  ADD INDEX idx_logistics_trace_code (trace_code);

ALTER TABLE cart_item
  ADD UNIQUE KEY uk_cart_user_product (user_id, product_id);
