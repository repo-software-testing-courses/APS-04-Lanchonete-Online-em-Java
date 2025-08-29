-- Removendo a criação do banco, pois ele já é criado pelo docker-compose
-- CREATE DATABASE lanchonete
-- ENCODING = 'UTF-8'
-- TEMPLATE template0

---------------------------------------------

CREATE TABLE tb_status_lanchonete (
    id_status SERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tb_status_lanchonete (status) VALUES ('ABERTO');

CREATE TABLE tb_funcionarios(
	id_funcionario	SERIAL,
	nome			VARCHAR(30) CONSTRAINT nn_tb_func_nome NOT NULL,
	sobrenome		VARCHAR(30) CONSTRAINT nn_tb_func_sobrenome NOT NULL,
	usuario			VARCHAR(20) CONSTRAINT nn_tb_func_usuario NOT NULL,
	senha			TEXT CONSTRAINT nn_tb_func_usuario NOT NULL,
	cargo			VARCHAR(30) CONSTRAINT nn_tb_func_cargo NOT NULL,
	salario			NUMERIC(7,2) CONSTRAINT nn_tb_func_salario NOT NULL,
	cad_por			INTEGER,
	fg_ativo		INTEGER CONSTRAINT nn_tb_func_fg_ativo NOT NULL,
	CONSTRAINT pk_tb_func_id_func PRIMARY KEY(id_funcionario),
	CONSTRAINT fk_tb_func_id_func FOREIGN KEY(cad_por) REFERENCES tb_funcionarios(id_funcionario)
);

CREATE TABLE tb_enderecos(
	id_endereco		SERIAL,
	rua				VARCHAR(80) CONSTRAINT nn_tb_enderecos_rua NOT NULL,
	bairro			VARCHAR(50) CONSTRAINT nn_tb_enderecos_bairro NOT NULL,
	numero			INTEGER,
	complemento		VARCHAR(30),
	cidade			VARCHAR(50) CONSTRAINT nn_tb_enderecos_cidade NOT NULL,
	estado			CHAR(2) CONSTRAINT nn_tb_enderecos_estado NOT NULL,
	CONSTRAINT pk_tb_enderecos_id_endereco PRIMARY KEY(id_endereco)
);

CREATE TABLE tb_clientes(
	id_cliente		SERIAL,
	nome			VARCHAR(30) CONSTRAINT nn_tb_clientes_nome NOT NULL,
	sobrenome		VARCHAR(30) CONSTRAINT nn_tb_clientes_sobrenome NOT NULL,
	telefone		VARCHAR(20) CONSTRAINT nn_tb_clientes_teledone NOT NULL,
	usuario			VARCHAR(30) CONSTRAINT nn_tb_clientes_usuario NOT NULL,
	senha			TEXT CONSTRAINT nn_tb_clientes_senha NOT NULL,
	fg_ativo		INTEGER CONSTRAINT nn_tb_clientes_fg_ativo NOT NULL,
	id_endereco		INTEGER,
	CONSTRAINT pk_tb_clientes_id_cliente PRIMARY KEY(id_cliente),
	CONSTRAINT fk_tb_clientes_id_endereco FOREIGN KEY(id_endereco) REFERENCES tb_enderecos(id_endereco)
);

CREATE TABLE tb_ingredientes(
	id_ingrediente	SERIAL,
	nm_ingrediente	VARCHAR(40) CONSTRAINT nn_tb_ingrd_nm_ingrd NOT NULL,
	descricao		TEXT CONSTRAINT nn_tb_ingrd_descricao NOT NULL,
	quantidade		INTEGER CONSTRAINT nn_tb_ingrd_qntd NOT NULL,
	valor_compra	NUMERIC(7,2) CONSTRAINT nn_tb_ingrd_valor_compra NOT NULL,
	valor_venda		NUMERIC(7,2) CONSTRAINT nn_tb_ingrd_valor_venda NOT NULL,
	tipo			VARCHAR(40) CONSTRAINT nn_tb_ingrd_tipo NOT NULL,
	fg_ativo		INTEGER CONSTRAINT nn_tb_ingrd_fg_ativo NOT NULL,
	CONSTRAINT pk_tb_ingrd_id_igrd PRIMARY KEY(id_ingrediente)
);

CREATE TABLE tb_lanches(
	id_lanche		SERIAL,
	nm_lanche		VARCHAR(30) CONSTRAINT nn_tb_lanches_nm_lanche NOT NULL,
	descricao		TEXT CONSTRAINT nn_tb_lanches_nm_lanche NOT NULL,
	valor_venda		NUMERIC(7,2) CONSTRAINT nn_tb_lanches_valor_venda NOT NULL,
	fg_ativo		INTEGER CONSTRAINT nn_tb_lanches_fg_ativo NOT NULL,
	CONSTRAINT pk_tb_lanches_id_lanche PRIMARY KEY(id_lanche)
);

CREATE TABLE tb_ingredientes_lanche(
	id_lanche		INTEGER,
	id_ingrediente	INTEGER,
	quantidade		INTEGER CONSTRAINT nn_tb_ingrd_lanche NOT NULL,
	CONSTRAINT pk_tb_ingrd_lanche_id_igrd_id_lanche PRIMARY KEY(id_ingrediente, id_lanche),
	CONSTRAINT fk_tb_ingrd_lanche_id_lanche FOREIGN KEY(id_lanche) REFERENCES tb_lanches(id_lanche),
	CONSTRAINT fk_tb_ingrd_lanche_id_ingrediente FOREIGN KEY(id_ingrediente) REFERENCES tb_ingredientes(id_ingrediente)
);

CREATE TABLE tb_pedidos(
	id_pedido		SERIAL,
	id_cliente		INTEGER CONSTRAINT nn_tb_pedidos_id_cliente NOT NULL,
	data_pedido		TEXT CONSTRAINT nn_tb_pedidos_data_pedido NOT NULL,
	valor_total		NUMERIC(7,2),
	CONSTRAINT pk_tb_pedidos_id_pedido PRIMARY KEY(id_pedido),
	CONSTRAINT fk_tb_pedidos_id_cliente FOREIGN KEY(id_cliente) REFERENCES tb_clientes(id_cliente)
);

CREATE TABLE tb_bebidas(
	id_bebida		SERIAL,
	nm_bebida		VARCHAR(30) CONSTRAINT nn_tb_bebidas_nm_bebida NOT NULL,
	descricao		TEXT CONSTRAINT nn_tb_bebidas_descricao NOT NULL,
	quantidade		INTEGER CONSTRAINT nn_tb_bebidas_quantidade NOT NULL,
	valor_compra	NUMERIC(7,2) CONSTRAINT nn_tb_bebidas_valor_compra NOT NULL,
	valor_venda		NUMERIC(7,2) CONSTRAINT nn_tb_bebidas_valor_venda NOT NULL,
	tipo			VARCHAR(40) CONSTRAINT nn_tb_bebidas_tipo NOT NULL,
	fg_ativo		INTEGER CONSTRAINT nn_tb_bebidas_fg_ativo NOT NULL,
	CONSTRAINT pk_tb_bebidas_id_bebida PRIMARY KEY(id_bebida)
);

CREATE TABLE tb_lanches_pedido(
	id_pedido		INTEGER,
	id_lanche		INTEGER,
	quantidade		INTEGER CONSTRAINT nn_tb_lanches_pedido NOT NULL,
	CONSTRAINT pk_tb_lp_id_pedido_lanche_ PRIMARY KEY(id_pedido, id_lanche),
	CONSTRAINT fk_tb_lp_id_lanche FOREIGN KEY(id_lanche) REFERENCES tb_lanches(id_lanche)
);

CREATE TABLE tb_bebidas_pedido(
	id_pedido		INTEGER,
	id_bebida		INTEGER,
	quantidade		INTEGER CONSTRAINT nn_tb_bebidas_pedido NOT NULL,
	CONSTRAINT pk_tb_bebidas_pedido PRIMARY KEY(id_pedido, id_bebida),
	CONSTRAINT fk_tb_bp_id_bebida FOREIGN KEY(id_bebida) REFERENCES tb_bebidas(id_bebida)
);

CREATE TABLE tb_tokens(
	id_token    SERIAL,
	token       TEXT CONSTRAINT tb_tokens_token_nn NOT NULL UNIQUE,
	CONSTRAINT pk_tb_tokens_id_token PRIMARY KEY (id_token)
);

-- Usuário admin padrão
-- senha: admin (hash MD5: 21232f297a57a5a743894a0e4a801fc3)
INSERT INTO tb_funcionarios (nome, sobrenome, usuario, senha, cargo, salario, cad_por, fg_ativo)
VALUES ('Admin', 'Master', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin', 9999.99, NULL, 1);


INSERT INTO tb_ingredientes (nm_ingrediente, descricao, quantidade, valor_compra, valor_venda, tipo, fg_ativo) VALUES
('Pão de Hambúrguer', 'Pão tradicional para hambúrguer', 100, 0.50, 1.00, 'Pão', 1),
('Carne Bovina 150g', 'Hambúrguer de carne bovina 150g', 50, 3.00, 6.00, 'Proteína', 1),
('Queijo Cheddar', 'Fatia de queijo cheddar', 80, 0.80, 1.50, 'Queijo', 1),
('Alface', 'Folhas de alface fresca', 30, 0.20, 0.50, 'Vegetal', 1),
('Tomate', 'Fatias de tomate fresco', 40, 0.30, 0.60, 'Vegetal', 1),
('Cebola', 'Fatias de cebola roxa', 25, 0.15, 0.40, 'Vegetal', 1),
('Bacon', 'Fatias de bacon crocante', 35, 1.20, 2.50, 'Proteína', 1),
('Molho Especial', 'Molho especial da casa', 60, 0.30, 0.80, 'Molho', 1),
('Maionese', 'Maionese tradicional', 50, 0.25, 0.60, 'Molho', 1),
('Ketchup', 'Molho ketchup', 45, 0.20, 0.50, 'Molho', 1),
('Mostarda', 'Molho mostarda', 40, 0.25, 0.55, 'Molho', 1),
('Picles', 'Picles em fatias', 30, 0.40, 0.80, 'Conserva', 1),
('Batata Frita', 'Porção de batata frita', 70, 1.50, 4.00, 'Acompanhamento', 1),
('Cebola Caramelizada', 'Cebola doce caramelizada', 20, 0.80, 1.80, 'Vegetal', 1),
('Frango Grelhado', 'Peito de frango grelhado', 40, 2.50, 5.50, 'Proteína', 1);


INSERT INTO tb_lanches (nm_lanche, descricao, valor_venda, fg_ativo) VALUES
('X-Burguer', 'Hambúrguer tradicional com carne, queijo, alface e tomate', 12.90, 1),
('X-Bacon', 'Hambúrguer com carne, bacon, queijo, alface e tomate', 15.90, 1),
('X-Frango', 'Hambúrguer de frango grelhado com queijo, alface e tomate', 13.90, 1),
('X-Especial', 'Hambúrguer especial com carne, bacon, queijo, molho especial', 18.90, 1),
('X-Veggie', 'Hambúrguer vegetariano com queijo, alface, tomate e molhos', 11.90, 1);

INSERT INTO tb_ingredientes_lanche (id_lanche, id_ingrediente, quantidade) VALUES
(1, 1, 1), -- Pão
(1, 2, 1), -- Carne
(1, 3, 1), -- Queijo
(1, 4, 2), -- Alface
(1, 5, 2), -- Tomate
(1, 9, 1); -- Maionese

INSERT INTO tb_ingredientes_lanche (id_lanche, id_ingrediente, quantidade) VALUES
(2, 1, 1), -- Pão
(2, 2, 1), -- Carne
(2, 7, 2), -- Bacon
(2, 3, 1), -- Queijo
(2, 4, 2), -- Alface
(2, 5, 2), -- Tomate
(2, 9, 1); -- Maionese

INSERT INTO tb_ingredientes_lanche (id_lanche, id_ingrediente, quantidade) VALUES
(3, 1, 1), -- Pão
(3, 15, 1), -- Frango
(3, 3, 1), -- Queijo
(3, 4, 2), -- Alface
(3, 5, 2), -- Tomate
(3, 9, 1); -- Maionese

INSERT INTO tb_ingredientes_lanche (id_lanche, id_ingrediente, quantidade) VALUES
(4, 1, 1), -- Pão
(4, 2, 1), -- Carne
(4, 7, 2), -- Bacon
(4, 3, 1), -- Queijo
(4, 14, 1), -- Cebola Caramelizada
(4, 8, 1), -- Molho Especial
(4, 12, 2); -- Picles

INSERT INTO tb_ingredientes_lanche (id_lanche, id_ingrediente, quantidade) VALUES
(5, 1, 1), -- Pão
(5, 3, 1), -- Queijo
(5, 4, 3), -- Alface
(5, 5, 3), -- Tomate
(5, 6, 2), -- Cebola
(5, 9, 1), -- Maionese
(5, 11, 1); -- Mostarda

INSERT INTO tb_bebidas (nm_bebida, descricao, quantidade, valor_compra, valor_venda, tipo, fg_ativo) VALUES
('Coca-Cola 350ml', 'Refrigerante Coca-Cola lata 350ml', 100, 1.50, 4.50, 'Refrigerante', 1),
('Pepsi 350ml', 'Refrigerante Pepsi lata 350ml', 80, 1.40, 4.30, 'Refrigerante', 1),
('Guaraná Antarctica 350ml', 'Refrigerante Guaraná Antarctica lata 350ml', 90, 1.45, 4.40, 'Refrigerante', 1),
('Fanta Laranja 350ml', 'Refrigerante Fanta Laranja lata 350ml', 70, 1.40, 4.30, 'Refrigerante', 1),
('Sprite 350ml', 'Refrigerante Sprite lata 350ml', 75, 1.40, 4.30, 'Refrigerante', 1),
('Suco de Laranja 300ml', 'Suco natural de laranja 300ml', 40, 2.00, 5.50, 'Suco Natural', 1),
('Suco de Uva 300ml', 'Suco natural de uva 300ml', 35, 2.20, 5.70, 'Suco Natural', 1),
('Água Mineral 500ml', 'Água mineral sem gás 500ml', 60, 0.80, 2.50, 'Água', 1),
('Água com Gás 500ml', 'Água mineral com gás 500ml', 45, 1.00, 2.80, 'Água', 1),
('Milkshake Chocolate', 'Milkshake cremoso sabor chocolate', 25, 3.50, 8.90, 'Milkshake', 1),
('Milkshake Morango', 'Milkshake cremoso sabor morango', 25, 3.50, 8.90, 'Milkshake', 1),
('Café Expresso', 'Café expresso tradicional', 50, 0.60, 3.50, 'Café', 1);