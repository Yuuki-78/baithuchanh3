CREATE DATABASE soa_product;
GO

USE soa_product;
GO

CREATE TABLE products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Thêm dữ liệu test (2 bản ghi cũ + 10 bản ghi mới)
INSERT INTO products (name, description, price, quantity) VALUES 
(N'Laptop Dell XPS', N'Laptop van phong cao cap', 25000000.00, 10),
(N'Ban phim Co Keychron', N'Ban phim co Bluetooth', 2000000.00, 25),
(N'Chuot Khong Day Logitech', N'Chuot wireless ergonomic', 850000.00, 50),
(N'Man Hinh LG UltraWide 29 inch', N'Man hinh cho do hoa va giai tri', 6500000.00, 15),
(N'Tai Nghe Sony WH-1000XM5', N'Tai nghe chong on chu dong ANC', 7900000.00, 20),
(N'Loa Bluetooth JBL Flip 6', N'Loa chong nuoc di dong', 2400000.00, 30),
(N'O Cung SSD Samsung 1TB', N'Nvme M.2 PCIe Gen 4', 2100000.00, 40),
(N'Ram Corsair Vengeance 16GB', N'DDR4 3200MHz', 1200000.00, 35),
(N'Sac Du Phong Anker 20000mAh', N'Ho tro sac nhanh PD 20W', 950000.00, 60),
(N'Webcam Logitech C920', N'Do phan giai Full HD 1080p', 1600000.00, 18),
(N'Ghe Van Phong Ergonomic', N'Ghe luoi nga lung thoai mai', 3200000.00, 12),
(N'Hub chuyen doi Type-C 7 trong 1', N'Cong HDMI, USB 3.0, SD Card', 650000.00, 45);
GO