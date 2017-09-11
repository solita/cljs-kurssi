CREATE TABLE category (
  id SERIAL PRIMARY KEY,
  name VARCHAR (100) NOT NULL,
  description TEXT
);

CREATE TABLE product (
  id SERIAL PRIMARY KEY,
  name VARCHAR (100) NOT NULL,
  description TEXT,

  -- price per unit (max value 100000,99)
  price NUMERIC(8,2)
);

CREATE TABLE product_category (
  product_id INTEGER NOT NULL REFERENCES product (id),
  category_id INTEGER NOT NULL REFERENCES category (id)
);

-- insert some test data

INSERT INTO category (name, description)
VALUES ('Toys','All sorts of toys for children and childish adults.'),
       ('Clothing, outdoors', 'All outdoor apparel like boots and jackets.'),
       ('Doomsday devices', 'Sometimes you just want to watch the world burn and we have just the items for you!');

INSERT INTO product (name, description, price)
VALUES ('Illudium Q-36 Explosive Space Modulator', 'Pesky planets obstructing your view? Just blow them up with this handy space modulator!', 95990.50),
       ('Log! from Blammo', 'It''s log, it''s log, it''s big, it''s heavy, it''s wood.', 19.95),
       ('Don''t whizz on the electric fence!', 'Fun board game for the whole family.', 59.50),
       ('Fine leather jacket', 'I''m selling these fine leather jackets.', 500.0);

INSERT INTO product_category (product_id, category_id)
VALUES (1, 3), (2, 1), (3, 1), (4, 2);
