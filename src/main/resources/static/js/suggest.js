/**
 * 
 */
$(function() {
	$("#search-name").on("keyup", function() {
			var hostUrl = "http://localhost:8080/employee/suggest";
			$.ajax({
				url: hostUrl,
				type: "POST",
				datatype: "json",
			async: true
			}).done(function(data) {
				console.log(data);
				    $( "#search-name" ).autocomplete({
				      source: data.employeeNameList
				    });
			}).fail(function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("XMLHttpRequest : " + XMLHttpRequest.status);
				console.log("textStatus : " + textStatus); 
				console.log("errorThrown : " + errorThrown.message);
			});
	  });
})