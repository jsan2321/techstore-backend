ALTER TABLE product ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0 NOT NULL;

ALTER TABLE product ADD CONSTRAINT chk_product_stock_non_negative CHECK (stock >= 0);

ALTER TABLE cart_items ADD CONSTRAINT chk_cart_item_quantity_positive CHECK (quantity > 0);
ALTER TABLE order_items ADD CONSTRAINT chk_order_item_quantity_positive CHECK (quantity > 0);

CREATE INDEX IF NOT EXISTS idx_orders_status_date ON orders (status, order_date);
