document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token"); // 토큰 확인
    const authButton = document.getElementById("logoutButton"); // 버튼 그대로 사용

    if (!authButton) return;


    if (token) {
        // 로그인 상태
        authButton.textContent = "로그아웃";
        authButton.onclick = () => {
            localStorage.removeItem("token");
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

});