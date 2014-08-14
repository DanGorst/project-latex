'use strict';

function refreshPage() {
	location.reload();
}

function updateTimestamp() {
	document.getElementById('timestamp').innerHTML = new Date();
}

setInterval(refreshPage, 5000);

window.onload = updateTimestamp;