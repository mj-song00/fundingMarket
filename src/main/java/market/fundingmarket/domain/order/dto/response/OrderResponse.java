//package market.fundingmarket.domain.order.dto.response;
//
//import lombok.Getter;
//import market.fundingmarket.domain.file.entity.File;
//import market.fundingmarket.domain.order.entity.Order;
//import market.fundingmarket.domain.project.dto.response.ProjectResponse;
//import market.fundingmarket.domain.project.entity.Project;
//import market.fundingmarket.domain.reward.entity.Reward;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Getter
//public class OrderResponse {
//
//    private final Long id;
//    private final String paymentKey;
//    private final int totalAmount;
//    private final LocalDateTime approvedAt;
//    private final List<ProjectInfo> project;
//    private final List<ImageInfo> image;
//    private final List<RewardInfo> reward;
//
//    @Getter
//    public static class ProjectInfo{
//        private final Long id;
//        private final String title;
//        private final String expectedDeliveryDate;
//
//        public ProjectInfo(Project project){
//            this.id = project.getId();
//            this.title = project.getTitle();
//            this.expectedDeliveryDate= project.getExpectedDeliveryDate();
//        }
//
//    }
//
//    @Getter
//    public static class ImageInfo{
//        private final Long id;
//        private final String thumnailUrl;
//
//        public ImageInfo(File file){
//            this.id = file.getId();
//            this.thumnailUrl = file.getImageUrl();
//        }
//    }
//
//    @Getter
//    public static class RewardInfo{
//        private final Long id;
//        private final String description;
//        private final Long price;
//
//        public RewardInfo(Reward reward) {
//            this.id = reward.getId();
//            this.description = reward.getDescription();
//            this.price = reward.getPrice();
//        }
//    }
//
//    public OrderResponse(Order order, List<Project> project ,List<File> image, List<Reward> reward ){
//        this.id = order.getId();
//        this.paymentKey = order.getPaymentKey();
//        this.totalAmount = order.getTotalAmount();
//        this.approvedAt = order.getApprovedAt();
//        this.project = project.stream().map(OrderResponse.ProjectInfo::new).toList();
//        this.image = image.stream().map(OrderResponse.ImageInfo::new).toList();
//        this.reward = reward.stream().map(OrderResponse.RewardInfo::new).toList();
//    }
//}
