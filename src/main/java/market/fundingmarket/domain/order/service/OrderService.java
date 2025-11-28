package market.fundingmarket.domain.order.service;

import market.fundingmarket.domain.order.dto.response.OrderResponse;
import market.fundingmarket.domain.user.dto.AuthUser;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getOrders(AuthUser authUser);
}
