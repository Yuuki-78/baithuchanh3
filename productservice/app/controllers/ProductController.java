package controllers;

import models.Product;
import play.db.Database;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@With(JwtAction.class) // Đảm bảo xác thực qua JWT Token của Bài 2
public class ProductController extends Controller {

    private final Database db;

    @Inject
    public ProductController(Database db) {
        this.db = db;
    }

    // GET /products - Lấy tất cả sản phẩm
    public Result getAll() {
        List<Product> products = new ArrayList<>();
        db.withConnection(conn -> {
            String sql = "SELECT * FROM products";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        });
        return ok(Json.toJson(products));
    }

    // GET /products/:id - Lấy chi tiết 1 sản phẩm
    public Result getById(Integer id) {
        Product product = db.withConnection(conn -> {
            String sql = "SELECT * FROM products WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToProduct(rs);
                    }
                }
            }
            return null;
        });

        if (product != null) {
            return ok(Json.toJson(product));
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sản phẩm id: " + id));
    }

    // POST /products - Thêm sản phẩm mới
    public Result create(Http.Request request) {
        var json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject().put("error", "Dữ liệu phải là JSON"));
        }

        String name = json.has("name") ? json.get("name").asText() : "";
        String description = json.has("description") ? json.get("description").asText() : "";
        BigDecimal price = json.has("price") ? new BigDecimal(json.get("price").asText()) : BigDecimal.ZERO;
        Integer quantity = json.has("quantity") ? json.get("quantity").asInt() : 0;

        Boolean success = db.withConnection(conn -> {
            String sql = "INSERT INTO products (name, description, price, quantity, created_at, updated_at) VALUES (?, ?, ?, ?, GETDATE(), GETDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setBigDecimal(3, price);
                stmt.setInt(4, quantity);
                return stmt.executeUpdate() > 0;
            }
        });

        if (Boolean.TRUE.equals(success)) {
            return created(Json.newObject().put("message", "Thêm sản phẩm thành công"));
        }
        return internalServerError(Json.newObject().put("error", "Không thể thêm sản phẩm"));
    }

    // PUT /products/:id - Cập nhật sản phẩm
    public Result update(Integer id, Http.Request request) {
        var json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject().put("error", "Dữ liệu phải là JSON"));
        }

        String name = json.has("name") ? json.get("name").asText() : null;
        String description = json.has("description") ? json.get("description").asText() : null;
        BigDecimal price = json.has("price") ? new BigDecimal(json.get("price").asText()) : null;
        Integer quantity = json.has("quantity") ? json.get("quantity").asInt() : null;

        Boolean success = db.withConnection(conn -> {
            String sql = "UPDATE products SET name = ISNULL(?, name), description = ISNULL(?, description), " +
                         "price = ISNULL(?, price), quantity = ISNULL(?, quantity), updated_at = GETDATE() WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setObject(3, price);
                stmt.setObject(4, quantity);
                stmt.setInt(5, id);
                return stmt.executeUpdate() > 0;
            }
        });

        if (Boolean.TRUE.equals(success)) {
            return ok(Json.newObject().put("message", "Cập nhật sản phẩm thành công id: " + id));
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sản phẩm id: " + id));
    }

    // DELETE /products/:id - Xóa sản phẩm
    public Result delete(Integer id) {
        Boolean success = db.withConnection(conn -> {
            String sql = "DELETE FROM products WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        });

        if (Boolean.TRUE.equals(success)) {
            return ok(Json.newObject().put("message", "Xóa sản phẩm thành công id: " + id));
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sản phẩm id: " + id));
    }

    // Ánh xạ dữ liệu từ SQL Server sang đối tượng Java Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}