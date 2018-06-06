
//start_conversation();
var conversation_start_flag = 0;
function start_conversation(){
	// The following ajax call will replace the socket
	if(conversation_start_flag == 0) {
		document.getElementById('messageList').innerHTML = "";
		var json_data_1 = JSON.stringify("");			
		$.ajax({
			url: "/chat_message",
			type: 'POST',
			async: false,
			data: { 
						param_1: json_data_1				
					},					
			success: function (message) {
				var dNow = new Date();				
				time = dNow.getHours() + ':' + dNow.getMinutes() + ':' + dNow.getSeconds();
				$("#messageList").append( '<ul id="watsonChat"><img src="static/img/cisco-chat-logo.png" style="height:38px;width:38px;"> ' + message+ '</ul>');
				$("#messageList").scrollTop($("#messageList")[0].scrollHeight);
				$("#messageList").removeClass("w3-light-gray");
				$("#messageText").prop('disabled', false);
				$("#messageText").focus();
				//console.log("message event triggered for: " + message['response']);		
				//console.log("message event triggered for: " +  message);					
			},
			
			error: function(xhr, textStatus, errorThrown){								
				//console.log(xhr);								
				//console.log(errorThrown);
				alert(errorThrown);
				alert(xhr.responseText);
			}				
		});       
		conversation_start_flag = 1;
	}		
	
}

$(function(){      

        $("#sendMessage").on("click", function() {				
            sendMessage();
        });
 
 
        $("#messageText").keyup(function(e){
            if(e.keyCode == 13)
            {				
                sendMessage();
            }
        });	 
 
        function sendMessage() {
            if ( $.trim($("#messageText").val()) ) 
            {
                var dNow = new Date();
                time = dNow.getHours() + ':' + dNow.getMinutes() + ':' + dNow.getSeconds();
               // $("#messageList").append( '<ul><font color="red">Me: </font>' +$("#messageText").val()+ '</ul>');
				document.getElementById("messageList").innerHTML += '<ul id="userChat"><font color="red">Me: </font> '+$("#messageText").val()+ '</ul>';
                $("#messageList").scrollTop($("#messageList")[0].scrollHeight);				
				
				var msg = $.trim($("#messageText").val());	
				
				$("#messageText").val('');
                $("#messageText").prop('disabled', true); 
				
				//setTimeout(function(){}, 3500); 
				
				//call_ajax(msg);
				
				var json_data_1 = JSON.stringify(msg);	

				$.ajax({
					url: "/chat_message",
					type: 'POST',
					async: true,
					data: { 
								param_1: json_data_1				
							},	
					
					success: function (message) {
						var dNow = new Date();
						time = dNow.getHours() + ':' + dNow.getMinutes() + ':' + dNow.getSeconds();
						$("#messageList").append( '<ul id="responseChat"><img src="static/img/cisco-chat-logo.png" style="height:38px;width:38px;"> ' +message['response']+ '</ul>');
						$("#messageList").scrollTop($("#messageList")[0].scrollHeight);
						$("#messageList").removeClass("w3-light-gray");
						$("#messageText").prop('disabled', false);
						$("#messageText").focus();
						//console.log("message event triggered for: " + message['response']);								
					},
					
					error: function(xhr, textStatus, errorThrown){								
						//console.log(xhr);								
						//console.log(errorThrown);
						alert(errorThrown);
						alert(xhr.responseText);
					}					
				});  
								
            }
            else
                $("#messageText").focus(); 
        }

        $("#btnRefresh").on("click", function() {
            $("#messageText").prop('disabled', true);
            $("#messageList").html('<ul id="list"></ul>');
            $("#messageList").addClass("w3-light-gray");
           // console.log("sending empty message ''");
            socket.send('');
        });

    });
	
	
