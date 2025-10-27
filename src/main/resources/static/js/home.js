document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("accessToken"); // 토큰 확인
    const authButton = document.getElementById("logoutButton"); // 버튼 그대로 사용

    if (!authButton) return;


    if (token) {
        // 로그인 상태
        authButton.textContent = "로그아웃";
        authButton.onclick = () => {
            localStorage.removeItem("accessToken");
            location.reload();
        };
    } else {
        // 비로그인 상태
        authButton.textContent = "로그인";
        authButton.onclick = () => {
            // 로그인 페이지로 이동
            const currentUrl = encodeURIComponent(window.location.href);
            window.location.href = `/login.html?redirect=${currentUrl}`;
        };
    }


    // 기본 카테고리
    const defaultCategoryKey = "WEBTOON";
    // 1. 카테고리 불러오기
    fetch('/api/v1/project/category')
        .then(response => response.json())
        .then(categories => {
            if (!categories || categories.length === 0) return;

            const categoryButtons = document.getElementById("categoryButtons");

            categories.forEach(category => {
                const btn = document.createElement("button");
                btn.className = "btn btn-outline-primary m-1 px-3 py-1";
                btn.innerText = category.value;

                btn.onclick = () => {
                    // 버튼 강조
                    document.querySelectorAll('#categoryButtons button').forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');

                    fetchProjectsByCategory(category.key);
                };

                categoryButtons.appendChild(btn);
            });

            // 기본 카테고리 자동 로드
            const defaultCategory = categories.find(c => c.key === defaultCategoryKey);
            if (defaultCategory) {
                const defaultBtn = Array.from(categoryButtons.children)
                    .find(b => b.innerText === defaultCategory.value);
                defaultBtn.classList.add('active');
                fetchProjectsByCategory(defaultCategory.key);
            }
        })
        .catch(error => console.error("카테고리 불러오는 중 오류:", error));

// 2. 카테고리별 프로젝트 조회
    function fetchProjectsByCategory(categoryKey) {
        fetch(`/api/v1/project/category/${categoryKey}`)
            .then(response => response.json())
            .then(data => {
                if (Array.isArray(data)) {
                    renderProjects(data); // 배열 그대로 전달
                } else if (data.data) {
                    renderProjects(data.data); // 기존 구조도 지원
                } else {
                    console.warn("프로젝트 데이터 없음", data);
                }
            })
    }

// 3. 프로젝트 카드 렌더링
    function renderProjects(projects) {
        const projectList = document.getElementById("projectList");
        projectList.innerHTML = ""; // 기존 카드 제거

        projects.forEach(project => {
            const col = document.createElement("div");
            col.classList.add("col");

            col.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${project.thumbnailUrl || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="${project.title}">
                <div class="card-body">
                    <h5 class="card-title">${project.title}</h5>
                    <p class="card-text">${project.contents || ''}</p>
                </div>
                <div class="card-footer">
                    <a href="/projectDetail.html?projectId=${project.id}" class="text-blue-500 hover:text-blue-700">상세보기</a>
                </div>
            </div>
        `;

            // 카드 클릭 시 상세조회 페이지로 이동
            col.querySelector('.card').addEventListener('click', () => {
                window.location.href = `/projectDetail.html?projectId=${project.id}`;
            });

            projectList.appendChild(col);
        });
    }


    // 로그아웃
    const logoutButton = document.getElementById("logoutButton");
    logoutButton.addEventListener("click", function () {
        localStorage.removeItem("token");
        window.location.href = "/home.html";
    });
});