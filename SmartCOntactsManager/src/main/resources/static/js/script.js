console.log("this js file");

const toggleSidebar = () => {

	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");

	}
	else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");

	}
};

function deleteContact(cId) {
	swal({
		title: "Are you sure?",
		text: "You want to delete this Contact..!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete/" + cId;
			} else {
				swal("Your Contact  is safe!");
			}
		});

}


const search = () => {

	let query = $("#search-input").val();
	console.log(query);

	if (query == "") {
		$(".search-result").hide();
	}
	else {
		/*console.log(query);*/
		
		let url='http://localhost:8281/search/'+query;
		
		fetch(url)
		.then((response)=>{
			return response.json();
			
		})
		.then((data)=>{
			
			
			let  text="<div class='list-group' >";
			
			data.forEach((contact)=>{
				
				text+="<a href='/user/"+contact.cId+"/contact' class='list-group-item list-group-item-action'> "+contact.name+"</a>"
			});
			
			text +="</div>";
			$(".search-result").html(text);
			$(".search-result").show();
			
		});
		
		
	}

};

