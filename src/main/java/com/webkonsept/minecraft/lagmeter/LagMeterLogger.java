package main.java.com.webkonsept.minecraft.lagmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LagMeterLogger {
	static LagMeter plugin;

	private static final String fileSeparator = System.getProperty("file.separator");

	private String error = "*shrug* Dunno.";
	private boolean logMemory = true;
	private boolean logTPS = true;
	protected static boolean enabled = false;
	private String timeFormat = "MM-dd-yyyy HH:mm:ss";

	String datafolder = "LagMeter";
	File logfile;
	PrintWriter log;

	LagMeterLogger (LagMeter instance,boolean enable){
		plugin = instance;
		if(enable){
			this.enable();
		}
	}
	LagMeterLogger (LagMeter instance){
		plugin = instance;
	}


	public boolean enable(File logTo){
		logfile = logTo;
		return beginLogging();
	}
	public boolean enable(){
		if(!plugin.useLogsFolder){
			System.out.println("[LagMeter] Not using logs folder.");
			return this.enable(new File(plugin.getDataFolder(),"lag.log"));
		}else{
			System.out.println("[LagMeter] Using logs folder. This will create a new log for each day (it might log data from tomorrow in today's file if you leave the server running without reloading/restarting).");
			return this.enable(new File("plugins"+fileSeparator+"LagMeter"+fileSeparator+"logs", "LagMeter-"+today()+".log"));
		}
	}
	public boolean enabled(){
		return enabled;
	}
	public void disable() throws IOException, FileNotFoundException, Exception {
		if(plugin.enableLogging = true)
			closeLog();
	}
	public void logMemory(boolean set){
		logMemory = set;
		if(logMemory == false && logTPS == false){
			try{
				this.disable();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			this.error("Both log outputs disabled: Logging disabled.");
		}
	}
	public boolean logMemory(){
		return logMemory;
	}
	public void logTPS(boolean set){
		logTPS = set;
		if (logMemory == false && logTPS == false){
			try {
				this.disable();
			}catch (FileNotFoundException e){
				e.printStackTrace();
			}catch (IOException e){
				e.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
			}
			this.error("Both log outputs disabled: Logging disabled.");
		}
	}
	public boolean logTPS(){
		return logTPS;
	}
	public String getError(){
		return this.error;
	}
	private void error(String errorMessage){
		this.error = errorMessage;
	}
	public String getTimeFormat(){
		return timeFormat;
	}
	public void setTimeFormat(String newFormat){
		timeFormat = newFormat;
	}
	public String getFilename(){
		if (logfile != null){
			return logfile.getAbsolutePath();
		}else{
			return "!! UNKNOWN !!";
		}
	}
	private boolean beginLogging(){
		boolean ret = true;
		if (logfile == null){
			error("Logfile is null");
			ret = false;
		}else if (logMemory == false && logTPS == false){
			error("Both logMemory and logTPS are disabled.  Nothing to log!");
			ret = false;
		}else{
			try{
				if(!logfile.exists()){
					logfile.createNewFile();
				}
				log = new PrintWriter(new FileWriter(logfile,true));
				log("Logging enabled.");
			}
			catch(IOException e){
				e.printStackTrace();
				error("IOException opening logfile!");
				ret = false;
			}
		}
		enabled = true;
		return ret;
	}
	private void closeLog() throws IOException, Exception, FileNotFoundException{
		if(enabled = true){
			log.flush();
			log.close();
			log = null;
			enabled = false;
		}
	}
	protected void log(String message){
		if(enabled){
			message = "["+now()+"] "+message;
			String newLine = plugin.newLineForLogStats?"\n":"  ";
			String players = plugin.playerLoggingEnabled?newLine+String.valueOf(Bukkit.getServer().getOnlinePlayers().length):"";
			log.println(message+players);
			if(plugin.logChunks){
				int totalChunks = 0;
				for(World world: Bukkit.getServer().getWorlds()){
					totalChunks += world.getLoadedChunks().length;
					if(!plugin.logTotalChunksOnly)
						log.print(newLine+"Chunks loaded in world \""+world.getName()+"\": "+world.getLoadedChunks().length);
				}
				log.println("Total chunks loaded: "+totalChunks);
			}
			if(plugin.logEntities){
				int totalEntities = 0;
				for(World world: Bukkit.getServer().getWorlds()){
					totalEntities += world.getEntities().size();
					if(!plugin.logTotalEntitiesOnly)
						log.print(newLine+"Entities in world \""+world.getName()+"\": "+world.getEntities().size());
				}
				log.println("Total entities: "+totalEntities);
			}
			if(plugin.newBlockPerLog)
				log.println();
			log.flush();
		}
	}
	public String now(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return sdf.format(cal.getTime());
	}
	public String today(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		return sdf.format(calendar.getTime());
	}
}