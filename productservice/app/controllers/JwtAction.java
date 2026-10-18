package controllers;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class JwtAction extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        // 1. Kiểm tra xem Request gửi lên có chứa Header "Authorization" không
        Optional<String> authHeader = req.header("Authorization");

        if (!authHeader.isPresent() || !authHeader.get().startsWith("Bearer ")) {
            // Trả về 401 Unauthorized nếu không có Token hoặc format không phải "Bearer <token>"
            return CompletableFuture.completedFuture(
                unauthorized(play.libs.Json.newObject().put("error", "Thiếu Token hoặc định dạng Header không hợp lệ!"))
            );
        }

        // 2. Trích xuất chuỗi JWT token (bỏ 7 ký tự "Bearer " ở đầu)
        String token = authHeader.get().substring(7);

        // 3. Giải mã và xác thực token bằng JwtUtil
        DecodedJWT jwt = JwtUtil.verifyToken(token);

        if (jwt == null) {
            // Trả về 401 Unauthorized nếu token sai chữ ký hoặc đã hết hạn
            return CompletableFuture.completedFuture(
                unauthorized(play.libs.Json.newObject().put("error", "Token không hợp lệ hoặc đã hết hạn!"))
            );
        }

        // 4. Token hợp lệ -> Cho phép request đi tiếp vào Controller xử lý dữ liệu sản phẩm
        return delegate.call(req);
    }
}