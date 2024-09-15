let navbar = document.querySelector(".header .flex .navbar");

document.querySelector("#menu-btn").onclick = () => {
	navbar.classList.toggle("active");
	profile.classList.remove("active");
};

let profile = document.querySelector(".header .flex .profile");

document.querySelector("#user-btn").onclick = () => {
	profile.classList.toggle("active");
	navbar.classList.remove("active");
};

window.onscroll = () => {
	profile.classList.remove("active");
	navbar.classList.remove("active");
};
function filterProducts() {
	const searchInput = document.getElementById('searchBox').value.toLowerCase();
	const products = document.querySelectorAll('.product');
	let anyProductVisible = false;

	products.forEach(product => {
		const productName = product.querySelector('.name').innerText.toLowerCase();
		if (productName.includes(searchInput)) {
			product.style.display = '';
			anyProductVisible = true;
		} else {
			product.style.display = 'none';
		}
	});

	const noItemsMessage = document.getElementById('noItemsMessage');
	if (anyProductVisible) {
		noItemsMessage.style.display = 'none';
	} else {
		noItemsMessage.style.display = 'block';
	}
}

document.getElementById('searchBox').addEventListener('input', filterProducts);



// Get the modal
var modal = document.getElementById("orderStatusModal");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close-btn")[0];

// When the user clicks the button, open the modal 
document.getElementById("orderForm").addEventListener("submit", function(event) {
	event.preventDefault(); // Prevent the default form submission
	modal.style.display = "block";
});

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
	modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
	if (event.target == modal) {
		modal.style.display = "none";
	}
}