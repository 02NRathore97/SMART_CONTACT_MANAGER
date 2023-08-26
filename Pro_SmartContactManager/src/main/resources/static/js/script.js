//alert("This is script file");
console.log("This is script file");

const toggleSidebar=()=>{
	if($('.sidebar').is(":visible")){
		//hide sidebar
		$('.sidebar').css("display","none");
		$('.content').css("margin-left","0%");
	}else{
		//display sidebar
		$('.sidebar').css("display","block");
		$('.content').css("margin-left","20%");
	}
	
};


const search=()=>{
	//console.log("hii");
let query = $("#search-input").val();

if(query == ""){
	$(".search-result").hide();	
}else{
	//console.log(query);
	
	//sending result to server
	let url = `http://localhost:8080/search/${query}`;
	
	fetch(url).then((response)=>{
		return response.json();
	}).then((data)=>{
	let text = `<div class='list-group'>`
		data.forEach((contact)=>{
			text+=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
		});
		text+=`</div>`;
		
		$(".search-result").html(text);
		$(".search-result").show();
		console.log(data);
	});
	

}
};