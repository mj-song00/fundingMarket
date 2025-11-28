package market.fundingmarket.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.order.dto.response.OrderResponse;
import market.fundingmarket.domain.order.service.OrderService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Orders", description = "주문 내역 관련 API")
@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(summary= "주문 목록 조회", description = "나의 주문 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @Auth AuthUser authUser
    ){
        List<OrderResponse> result = orderService.getOrders(authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS));
    }

}
