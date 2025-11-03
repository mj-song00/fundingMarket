async function payment(event){
    event.preventDefault();
    const orderId= document.getElementById("orderId").value;
    const paymentKey = document.getElementById("paymentKey").value;
    const amount = document.getElementById("amount").value;
    const errorMessage = document.getElementById("errorMessage");

    errorMessage.classList.add("hidden");
    errorMessage.textContent = "";

    try {
        const response = await fetch("")

    }catch(error){

    }
}
