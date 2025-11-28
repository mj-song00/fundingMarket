package market.fundingmarket.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.order.dto.response.OrderResponse;
import market.fundingmarket.domain.order.entity.Order;
import market.fundingmarket.domain.order.repository.OrderRepository;
import market.fundingmarket.domain.payment.entity.Payment;
import market.fundingmarket.domain.payment.repository.PaymentRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public  List<OrderResponse> getOrders(AuthUser authUser) {
        User user =  getUser(authUser.getId());

        // 한 유저가 여러 주문 가능
        List<Order> orders = orderRepository.findAllWithSponsorAndProjectByUserId(user.getId());
        if (orders.isEmpty()) {
            throw new BaseException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        // 각 주문마다 Payment 조회 후 DTO 변환
        return orders.stream()
                .map(order -> {
                    Payment payment = null;
                    if (order.getSponsor() != null && order.getSponsor().getPaymentKey() != null) {
                        payment = paymentRepository.findByPaymentKey(order.getSponsor().getPaymentKey())
                                .orElse(null);
                    }
                    return new OrderResponse(order, payment);
                })
                .toList();

    }

    private User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.USER) {
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }
}
