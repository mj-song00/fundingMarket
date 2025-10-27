

// URL에서 프로젝트 ID 가져오기 (예: detail.html?projectId=1)
const urlParams = new URLSearchParams(window.location.search);
const projectId = urlParams.get("projectId");

// 프로젝트 상세 정보 불러오기
async function loadProjectDetail() {
    try {
        const response = await fetch(`/api/v1/project/${projectId}`);
        if (!response || !response.ok) {
            throw new Error("프로젝트 정보를 불러올 수 없습니다.");
        }

        const result = await response.json();
        const data = result.data;

        // HTML 요소에 데이터 넣기
        document.getElementById("projectTitle").textContent = data.title || "제목 없음";
        document.getElementById("projectContents").textContent = data.contents || "내용 없음";
        document.getElementById("projectCategory").textContent = data.category || "-";
        document.getElementById("fundingSchedule").textContent = data.fundingSchedule || "-";
        document.getElementById("expectedDeliveryDate").textContent = data.expectedDeliveryDate || "-";

        if (data.creator) {
            const creatorEl = document.getElementById("creatorIntroduce");
            if (creatorEl) creatorEl.textContent = data.creator.introduce || "";
        }

        // 이미지와 리워드는 필요 시 render 함수 호출
        renderImages(data.images || []);
        renderRewards(data.rewards || []);

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// 이미지 렌더링 함수
function renderImages(images) {
    const container = document.getElementById("imageList");
    container.innerHTML = "";

    if (images.length === 0) {
        container.innerHTML = "<p>등록된 이미지가 없습니다.</p>";
        return;
    }

    images.forEach(image => {
        const img = document.createElement("img");
        img.src = image.url;
        img.alt = "프로젝트 이미지";
        img.classList.add("w-48", "h-48", "object-cover", "rounded", "shadow");
        container.appendChild(img);
    });
}

// 리워드 렌더링 함수
function renderRewards(rewards) {
    const rewardList = document.getElementById("rewardList");
    if (!rewardList) return;

    rewardList.innerHTML = ""; // 초기화

    rewards.forEach(reward => {
        const rewardItem = document.createElement("div");
        rewardItem.classList.add("reward-item", "mb-4", "p-4", "border", "rounded");

        rewardItem.innerHTML = `
            <p class="mb-2">${reward.description}</p>
            <p class="mb-2 font-bold">${reward.price === 0 ? "무료" : reward.price + "원"}</p>
            <button class="sponsor-btn bg-blue-500 text-white px-4 py-2 rounded">
                후원하기
            </button>
        `;

        // 버튼 클릭 이벤트
        rewardItem.querySelector(".sponsor-btn").addEventListener("click", () => {
            startTossPay(reward);
        });

        rewardList.appendChild(rewardItem);
    });
}

function startTossPay(reward) {
    const paymentUrl = `/payment/toss?rewardId=${reward.id}&amount=${reward.price}`;
    window.open(paymentUrl, "_blank");
}

// 페이지 로드 시 실행
document.addEventListener("DOMContentLoaded", loadProjectDetail);
