// 로그인 함수
async function login(event) {
    event.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("errorMessage");

    errorMessage.classList.add("hidden");
    errorMessage.textContent = "";

    try {
        const response = await fetch("api/v1/users/auth/sign-in", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({email, password}),
        });

        if (response.ok) {
            const token = response.headers.get("Authorization").replace("Bearer ",
                "");
            localStorage.setItem("token", token);
            window.location.href = "/home.html";
        } else {
            const result = await response.json();
            errorMessage.textContent = result.message || "로그인에 실패했습니다.";
            errorMessage.classList.remove("hidden");
        }
    } catch (error) {
        errorMessage.textContent = "서버와 연결할 수 없습니다.";
        errorMessage.classList.remove("hidden");
    }
}

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("loginForm").addEventListener("submit", login);
});