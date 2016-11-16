<!DOCTYPE html>
<html>
<head>
<title>Counters</title>
</head>
<body>
	<div>
		<input type="button"
			value="Create a counter" onclick="construct()" />
			<input type="button"
			value="Delete cookies" onclick="deleteAllCookies()" />
	</div>
	<div id="messages"></div>
	<div id="counters"></div>
	<div id="countersList"></div>
	<script type="text/javascript">
		var webSocket = new WebSocket(
				'ws://localhost:8080/CountDownWebAppTP/websocket');

		webSocket.onerror = function(event) 
		{
			onError(event)
		};

		webSocket.onopen = function(event) 
		{
			onOpen(event)
		};

		webSocket.onmessage = function(event) 
		{
			onMessage(event)
		};

		function onMessage(event) 
		{
			let message = event.data;

			console.log(message);
			//console.log(message);
			let infos = message.split('|');
			
			let cookie = document.cookie.split("=");
			
			//console.log('id:'+infos[1]);
			
			//console.log('ids:'+infos[1]+' cookie1:'+cookie[1]);

			if(infos[0] == "ids")
			{
				document.cookie = "ids="+infos[1]+"; expires=Thu, 18 Dec 2020 12:00:00 UTC; path=/;";
			}
			else if(infos[0] == "result")
			{
				if(document.getElementById(infos[1]) == null)
				{
					document.getElementById('countersList').innerHTML += '<div id='+infos[1]+'>'+infos[2]+'<br>'+infos[3]+'<br><input type="button" value ="modifier" onclick="modifier()" /><input type="button" value ="supprimer" onclick="supprimer('+infos[1]+')" /></div>';
				}
				else
				{
					document.getElementById(infos[1]).innerHTML = '<div id='+infos[1]+'>'+infos[2]+'<br>'+infos[3]+'<br><input type="button" value ="modifier" onclick="modifier()" /><input type="button" value ="supprimer" onclick="supprimer('+infos[1]+')" /></div>';
				}
			}
		}

		function onOpen(event) 
		{
			//console.log(document.cookie);
			//deleteCookie("ids");			
			

			let cookies = document.cookie;
			
			cookies = cookies.replace('=', '|');
			
			//console.log(cookies);
			
			if(cookies.length > 0)
			{
				webSocket.send(cookies);
			}
				
		}

		function onClose(event)
		{
			
		}
		
		function onError(event) 
		{
			//alert(event.data);
		}

		function start() 
		{
			let text = "ajouter|"+document.getElementById("countername").value+"|"+document.getElementById("language").value+"|"+document.getElementById("day").value+"/"+document.getElementById("month").value+"/"+document.getElementById("year").value+" "+document.getElementById("hour").value+":"+document.getElementById("minute").value+":"+document.getElementById("second").value;
	
console.log(text);

			webSocket.send(text);
			document.getElementById('counters').innerHTML = "";
			return false;
		}
		
		function construct()
		{
			document.getElementById('counters').innerHTML = 'Choose the counter name : <input type="text" id="countername" /><br />';
			document.getElementById('counters').innerHTML += 'Select your prefered localisation for the counter : <select id="language"><option value="Europe/London">Anglais Britannique</option><option value="America/New_York">Anglais Americain</option><option value="Europe/Paris">Français</option><option value="Europe/Moscow">Russe</option></select><br />';
			document.getElementById('counters').innerHTML += 'Select the end date of the counter : <select id="day"></select><select id="month"></select><select id="year"></select> <select id="hour"></select>:<select id="minute"></select>:<select id="second"></select>';
			document.getElementById('counters').innerHTML += '<div><input type="submit" value="Validate this counter" onclick="start()" /></div>';
			
			for (let i = 1; i <= 31; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('day').appendChild(opt);
			}
			
			for (let i = 1; i <= 12; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('month').appendChild(opt);
			}
			
			for (let i= 2016; i <= 2100; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('year').appendChild(opt);
			}
			
			for (let i = 0; i <= 23; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('hour').appendChild(opt);
			}
			
			for (let i = 0; i <= 59; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('minute').appendChild(opt);
			}
			
			for (let i = 0; i <= 59; i++)
			{
			    let opt = document.createElement('option');
			    opt.value = i;
			    opt.innerHTML = i;
			    document.getElementById('second').appendChild(opt);
			}
			//document.cookie = "id0=1; expires=Thu, 18 Dec 2020 12:00:00 UTC; path=/;";
			//document.cookie = "id1=2; expires=Thu, 18 Dec 2020 12:00:00 UTC; path=/;";
			
			//document.cookie = "ids=0,1,2,3,4,5,6; expires=Thu, 18 Dec 2020 12:00:00 UTC; path=/;";
			
			/*deleteCookie("2");
			deleteCookie("id");*/
			
		}
		
		function deleteCookie(name)
		{
			document.cookie = name+"=;expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;";
		}

		function deleteAllCookies()
		{
			let cookies = document.cookie.split("=");

			let ids = cookies[1].split(",");

			for(let i = 0; i < ids.length; i++)
			{
				supprimer(ids[i]);
			}

			document.cookie = "ids=;expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;";
		}



		function modifier()
		{

		}

		function supprimer(id)
		{
			let text = "supprimer|"+id;
	
			console.log(text);

			webSocket.send(text);
		}

	</script>
</body>
</html>
