console.log("This is script file")

const toggleSidebar =()=> {
	 
    if($(".sidebar").is(":visible")){
    	$(".sidebar").css("display","none");
        $(".content").css("margin-left","0%")
        
    }
    else{
    	$(".sidebar").css("display","block");
        $(".content").css("margin-left","20%")
    }
};
//log out confirmation button
function confirmLogout() {
    return confirmLogout = confirm("Are you sure you want to logout?");
    
}
//confirm delete button
function confirmDelete() {
    return confirmDelete = confirm("Are you sure you want to delete?");
    
}



function myFunction1() {
	  var x = document.getElementById("myInput");
	  if (x.type === "password") {
	    x.type = "text";
	  } else {
	    x.type = "password";
	  }
	}

/* Search bar js starts*/
	const search =()=>{
		// console.log("searching...");
		let query=$("#search-input").val()
		
		if(query==''){
			$(".search-result").hide();
		}
		else{
			//search
			// console.log(query);
			$(".search-result").show();

			let url= `http://localhost:8080/search/${query}`;
			fetch(url).then((response)=>{
				return response.json();
			})
			.then((data)=>{
				// console.log(data);
				let text=`<div class='list-group'>`;

				data.forEach((contact) => {
					// text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.secondName}</a>`
					text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
					
					// text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.email}</a>`
					// text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.cId}</a>`
					
				});
				
				
				
				text+=`</div>`

				$(".search-result").html(text);
				$(".search-result").show();
				
			});
			

		}
	};
/* Search bar js ends */



