let authUser = {};
const token = localStorage.getItem("token");

async function getProfile(){
    try{
        const response = await fetch("http://localhost:8080/api/v1/users/me/profile", {
            method:"GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });


        if (!response.ok) {
            throw new Error("프로필 정보를 불러오지 못했습니다.");
        }

        const result = await response.json();

        authUser = { nickName: result.data.nickName, email: result.data.email };

        return authUser;

    }catch(error){
        console.error(error);
        alert(error.message);
    }
}