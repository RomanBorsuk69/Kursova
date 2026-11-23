DROP DATABASE IF EXISTS lab_maintenance;
CREATE DATABASE lab_maintenance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lab_maintenance;

CREATE TABLE specialist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(160) NOT NULL,
    specialization VARCHAR(120) NOT NULL,
    phone VARCHAR(30) NOT NULL
);

CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inventory_number VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    category VARCHAR(80) NOT NULL,
    laboratory VARCHAR(120) NOT NULL,
    criticality_level INT NOT NULL,
    responsible_specialist_id BIGINT NOT NULL,
    FOREIGN KEY (responsible_specialist_id) REFERENCES specialist(id)
);

CREATE TABLE service_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    fault_severity INT NOT NULL,
    expected_downtime_hours DOUBLE NOT NULL,
    state VARCHAR(30) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    priority_index DOUBLE NOT NULL,
    created_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
);

CREATE TABLE service_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    specialist_id BIGINT NOT NULL,
    work_description VARCHAR(500) NOT NULL,
    service_date VARCHAR(10) NOT NULL,
    labor_hours DOUBLE NOT NULL,
    cost DOUBLE NOT NULL,
    FOREIGN KEY (request_id) REFERENCES service_request(id),
    FOREIGN KEY (specialist_id) REFERENCES specialist(id)
);

INSERT INTO specialist(full_name, specialization, phone) VALUES
('Коваль Андрій Петрович', 'Оптичні прилади', '+380501112233'),
('Мельник Олена Ігорівна', 'Аналітичне обладнання', '+380672223344'),
('Шевченко Ігор Васильович', 'Електронні вимірювальні прилади', '+380933334455');

INSERT INTO equipment(inventory_number, name, category, laboratory, criticality_level, responsible_specialist_id) VALUES
('LAB-0001', 'Спектрофотометр UV-1800', 'Аналітичне', 'Лабораторія хімічного аналізу', 5, 2),
('LAB-0002', 'Мікроскоп МБС-10', 'Оптичне', 'Лабораторія матеріалознавства', 3, 1),
('LAB-0003', 'Осцилограф DS1054Z', 'Вимірювальне', 'Лабораторія електроніки', 4, 3),
('LAB-0004', 'Сушильна шафа СНОЛ-58', 'Термічне', 'Лабораторія хімічного аналізу', 2, 2);

INSERT INTO service_request(equipment_id, description, fault_severity, expected_downtime_hours, state, f1, f2, f3, priority_index, created_date) VALUES
(1, 'Нестабільні показники лампи', 4, 24, 'NEW', 100, 75, 33.33, 74.58, '2026-11-02'),
(2, 'Розбалансування механізму фокусування', 2, 36, 'DIAGNOSED', 50, 25, 50, 41.25, '2026-11-03'),
(3, 'Шум у каналі CH2', 3, 36, 'IN_SERVICE', 75, 50, 50, 60.00, '2026-11-05'),
(4, 'Несправний датчик температури', 1, 8, 'COMPLETED', 25, 0, 11.11, 12.78, '2026-10-20');

INSERT INTO service_record(request_id, specialist_id, work_description, service_date, labor_hours, cost) VALUES
(3, 3, 'Перевірка вхідного підсилювача каналу CH2', '2026-11-06', 2.5, 750.00),
(4, 2, 'Заміна датчика температури та калібрування', '2026-10-22', 1.5, 1200.00);
