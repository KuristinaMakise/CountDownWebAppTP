import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocket
{
	private SQLiteJDBC database;
	private ArrayList<String> listeIds;
	private final String HEURE_ZERO = "0a0m0j0h0m0s";

	public WebSocket()
	{
		database = new SQLiteJDBC();
		listeIds = new ArrayList<String>();
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException,
	InterruptedException
	{
		String messageSplit [] = message.split("\\|");

		if(messageSplit[0].equals("ajouter")) // action|nom|langue|dateDeFin dateDeFin exemple = 12/11/2016 10:34
		{
			afficher("ajouter");
			String value = "";
			for(int i=1;i<messageSplit.length-1;i++)
			{
				value += messageSplit[i]+"|";
			}
			value += messageSplit[messageSplit.length-1];

			int id = database.addCounter(value);
			listeIds.add(""+id);
			session.getBasicRemote().sendText(listeIdsToString()); // retourner ids|0,1,2,3,4,5,6
			new SendingThread(id,session);
		}
		else if(messageSplit[0].equals("modifier")) // modifier|id|nom|langue|dateDeFin
		{
			afficher("modifier");
			String value = "";
			for(int i=2;i<messageSplit.length-1;i++)
			{
				value += messageSplit[i]+"|";
			}
			value += messageSplit[messageSplit.length-1];
			
			int id = Integer.parseInt(messageSplit[1]);
			String compteur = database.getCounter(id);
			String compteurSplit[] = compteur.split("\\|"); // nom|langue|dateDeFin
			String tempsRestant = diff(compteurSplit[1],stringToDate(compteurSplit[2]));
			
			database.setCounter(Integer.parseInt(messageSplit[1]), value);
			
			if(tempsRestant.equals(HEURE_ZERO))
			{
				new SendingThread(id,session);
			}
		}
		else if(messageSplit[0].equals("supprimer")) // supprimer|id
		{
			afficher("supprimer");
			database.deleteCounter(Integer.parseInt(messageSplit[1]));
			listeIds.remove(""+Integer.parseInt(messageSplit[1]));
			
			if(listeIds.size() > 0)
			{
				session.getBasicRemote().sendText(listeIdsToString()); // retourner ids|0,1,2,3,4,5,6
			}
		}
		else if(messageSplit[0].equals("ids"))
		{
			afficher("ids");
			listeIds.clear();
			String ids[] = messageSplit[1].split(",");
			
			System.out.println("ids length:"+ids.length);
			
			for(String id:ids)
			{
				System.out.println("id :"+id);
				listeIds.add(id);
				new SendingThread(Integer.parseInt(id),session);
			}
		}
		else
		{
			System.err.println("Quelle action utiliser ?");
		}
	}

	@OnOpen
	public void onOpen()
	{
		System.out.println("Client connected");
	}

	@OnClose
	public void onClose()
	{
		System.out.println("Connection closed");
	}

	public void afficher(String str)
	{
		System.out.println("Le serveur : "+str);
	}

	public String listeIdsToString()
	{
		String ids = "ids|";
		for(int i=0;i<listeIds.size()-1;i++)
		{
			ids += listeIds.get(i)+",";
		}
		ids += listeIds.get(listeIds.size()-1);
		return ids;
	}

	public Date stringToDate(String message) // 12/11/2016 10:34
	{
		try
		{
			return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(message);
		} catch (ParseException e)
		{
			System.err.println("Erreur de conversion de stringToDate.");
		}
		return null;
	}
	
	public String diff(String zone, Date dateFin) // yyyy:MM:dd:HH:mm:ss
	{
		ZoneId zoneId = ZoneId.of(zone);
		
		Date dateLocaleDebut = new Date();
		LocalDateTime localDateTimeDebut = LocalDateTime.ofInstant(dateLocaleDebut.toInstant(), zoneId);
		Date dateZoneDebut = Date.from(localDateTimeDebut.atZone(ZoneId.systemDefault()).toInstant());
		
		if(dateFin.before(dateZoneDebut))
		{
			return HEURE_ZERO;
		}

		Calendar debutCalendar = new GregorianCalendar();
		debutCalendar.setTime(dateZoneDebut);
		Calendar finCalendar = new GregorianCalendar();
		finCalendar.setTime(dateFin);

		int moisRestants = 0;
		int firstDayInFirstMonth = debutCalendar.get(Calendar.DAY_OF_MONTH);
		debutCalendar.set(Calendar.DAY_OF_MONTH, 1);
		finCalendar.add(Calendar.DAY_OF_YEAR, -firstDayInFirstMonth+1);
		while(!debutCalendar.after(finCalendar))
		{     
			debutCalendar.add(Calendar.MONTH, 1);
			++moisRestants;
		}
		debutCalendar.add(Calendar.MONTH, -1); --moisRestants;
		int jour = 0;
		while(!debutCalendar.after(finCalendar))
		{
			debutCalendar.add(Calendar.DAY_OF_YEAR, 1);
			++jour;
		}
		debutCalendar.add(Calendar.DAY_OF_YEAR, -1);
		--jour;
		int lastMonthMaxDays = finCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if(jour >= lastMonthMaxDays)
		{
			++moisRestants;
			jour -= lastMonthMaxDays;
		}

		long annee = moisRestants/12;
		long mois = moisRestants%12;
		
		long secondesRestantes = (dateFin.getTime() - dateZoneDebut.getTime()) / 1000;
		
		long heure = (long) (secondesRestantes/3600.0)%24;
		long minute = (long) (secondesRestantes/60.0)%60;
		long seconde = (long) (secondesRestantes)%60;

		return annee+"a"+mois+"m"+jour+"j"+heure+"h"+minute+"m"+seconde+"s";
	}

	// Sending message to client each 1 second for each counter
	private class SendingThread extends Thread
	{
		private int threadId;
		private Session session;

		SendingThread(int threadId, Session session)
		{
			this.threadId = threadId;
			this.session = session;
			start();
		}

		public void run()
		{
			while(true)
			{
				if(!listeIds.contains(""+threadId)) break;
				String compteur = database.getCounter(threadId);
				String compteurSplit[] = compteur.split("\\|"); // nom|langue|dateDeFin
				
				String tempsRestant = diff(compteurSplit[1],stringToDate(compteurSplit[2]));
				try
				{
					session.getBasicRemote().sendText("result|"+threadId+"|"+compteurSplit[0]+"|"+tempsRestant);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if(tempsRestant.equals(HEURE_ZERO)) break;
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}
}
