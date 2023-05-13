CREATE TABLE orders (
  id UUID PRIMARY KEY,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  owner_id UUID NOT NULL
);

CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  product_id UUID NOT NULL,
  quantity INTEGER NOT NULL,
  order_id UUID NOT NULL
);

CREATE TABLE products (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(255) NOT NULL,
  quantity INTEGER NOT NULL
);
