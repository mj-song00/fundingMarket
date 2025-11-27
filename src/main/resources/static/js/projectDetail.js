// URL에서 프로젝트 ID 가져오기
const urlParams = new URLSearchParams(window.location.search);
const projectId = urlParams.get("projectId");

// 페이지 로드 시 실행
document.addEventListener("DOMContentLoaded", loadProjectDetail);

let rewardsData = []; // 모든 리워드 저장
let selectedReward = null;

// 프로젝트 상세 정보 불러오기
async function loadProjectDetail() {
    try {
        const response = await fetch(`/api/v1/project/${projectId}`);
        if (!response.ok) throw new Error("프로젝트 정보를 불러올 수 없습니다.");

        const result = await response.json();
        const data = result.data;

        rewardsData = data.rewards || [];

        document.getElementById("projectTitle").textContent = data.title || "제목 없음";
        document.getElementById("projectContents").textContent = data.contents || "내용 없음";
        document.getElementById("projectCategory").textContent = data.category || "-";
        document.getElementById("fundingSchedule").textContent = data.fundingSchedule || "-";
        document.getElementById("expectedDeliveryDate").textContent = data.expectedDeliveryDate || "-";

        if (data.creator) {
            document.getElementById("creatorIntroduce").textContent = data.creator.introduce || "";
        }

        sessionStorage.setItem("projectId",projectId);

        renderImages(data.images || []);
        renderRewards(rewardsData);

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// 이미지 렌더링
function renderImages(images) {
    const container = document.getElementById("imageList");
    container.innerHTML = "";
    if (!images.length) {
        container.innerHTML = "<p>등록된 이미지가 없습니다.</p>";
        return;
    }
    images.forEach(imgData => {
        const img = document.createElement("img");
        img.src = imgData.url;
        img.alt = "프로젝트 이미지";
        img.classList.add("w-48", "h-48", "object-cover", "rounded", "shadow");
        container.appendChild(img);
    });
}

// 리워드 렌더링
function renderRewards(rewards) {
    const rewardList = document.getElementById("rewardList");
    rewardList.innerHTML = "";

    rewards.forEach(reward => {
        const rewardItem = document.createElement("div");
        rewardItem.classList.add("reward-item");

        rewardItem.innerHTML = `
            <p class="mb-2">${reward.description}</p>
            <p class="mb-2 font-bold">${reward.price === 0 ? "무료" : reward.price + "원"}</p>
            <button class="sponsor-btn bg-blue-500 text-white px-4 py-2 rounded">후원하기</button>
        `;

        rewardItem.querySelector(".sponsor-btn").addEventListener("click", () => {
            sessionStorage.setItem("product_name", reward.description);
            sessionStorage.setItem("total", reward.price);
            sessionStorage.setItem("reward_id", reward.id);

            window.location.href = "/selectPayment.html";
        });

        rewardList.appendChild(rewardItem);
    });
}
