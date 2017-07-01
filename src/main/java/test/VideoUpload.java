package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.JSONException;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;

import twitter4j.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.steelhouse.twitter.ads.Account;
import com.steelhouse.twitter.ads.TwitterAdsClient;
import com.steelhouse.twitter.ads.client.ClientService;
import com.steelhouse.twitter.ads.client.ClientServiceFactory;
import com.steelhouse.twitter.ads.creative.Video;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class VideoUpload {
	
	
	/*
	 *  READ THIS -> https://dev.twitter.com/rest/public/uploading-media#chunkedupload
	 *  This encapsulates the 3 steps necessary to upload video onto Twitter.
	 *  
	 *  1. INIT - creates the metadata, (file type, and size of file in bytes ), returns a media_id
	 *  2. APPEND - using the media_id value from the INIT step, do the binary multi-part file POST 
	 *  3. FINALIZE - once the APPEND step is completed, confirms to Twitters system that the file upload is essentially done.
	 *  
	 */
	
	private static TwitterAdsClient getTwitterAdsClient() {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String consumerKey = properties.getProperty("consumer.key");
		String consumerSecret = properties.getProperty("consumer.secret");
		String accessToken = properties.getProperty("access.token");
		String accessSecret = properties.getProperty("access.secret");
		return new TwitterAdsClient(consumerKey, consumerSecret, accessToken, accessSecret);
	}

	final static String DOMAIN = "https://upload.twitter.com";
	final static String RESOURCE = "/1.1/media/upload.json";
	
	
	final static TwitterAdsClient client = getTwitterAdsClient();
	final static String ACCOUNT_ID = "18ce54aq4d5";
	
	
	
	//public static void main(String[] args) throws Exception {
	public void tweetTweetWithVideo(String urlText, String tweetContents) throws IOException, InterruptedException, TwitterException, JSONException {
		
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		ClientServiceFactory.consumerKey = properties.getProperty("consumer.key");
		ClientServiceFactory.consumerSecret = properties.getProperty("consumer.secret");
		ClientServiceFactory.accessToken = properties.getProperty("access.token");
		ClientServiceFactory.accessTokenSecret = properties.getProperty("access.secret");

		// String filePath = "C:/temp/doge.mp4";
		URL url = new URL(urlText);
		File file = new File("/home/jonathan/Downloads/ftmp.mp4");
		FileUtils.copyURLToFile(url, file);
		//String filePath = "/home/jonathan/Downloads/output.mp4";
		
		FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
		FFmpegProbeResult probeResult = ffprobe.probe("/home/jonathan/Downloads/ftmp.mp4");
		FFmpegFormat format = probeResult.getFormat();
		double fduration = format.duration;
		System.out.println(fduration);
		if (fduration > 29.5) {
			fduration = 29.0;
		}
		long fbitrate = (long) ((long) (4000000*8*.8)/(fduration));
		System.out.println(fbitrate);
		
		FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
		FFmpegBuilder builder = new FFmpegBuilder()
	
		.setInput("/home/jonathan/Downloads/ftmp.mp4")     // Filename, or a FFmpegProbeResult
		.overrideOutputFiles(true) // Override the output if it exists
		
		.addOutput("/home/jonathan/Downloads/ftmpo.mp4")// Filename for the destination
	
		//.setFormat("mp4")        // Format is inferred from filename, or can be set
		//.setTargetSize(4500000)  // Aim for a 250KB file
		.setDuration((long)fduration, TimeUnit.SECONDS)
		.setVideoBitRate(fbitrate)
		//.disableSubtitle()       // No subtiles

//		.setAudioChannels(1)         // Mono audio
//		.setAudioCodec("aac")        // using the aac codec
//		.setAudioSampleRate(48000)  // at 48KHz
//		.setAudioBitRate(32768)      // at 32 kbit/s
//
//		.setVideoCodec("libx264")     // Video using x264
//		.setVideoFrameRate(24, 1)     // at 24 frames per second
//		.setVideoResolution(640, 480) // at 640x480 resolution

		.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
		.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

		// Run a one-pass encode
		executor.createJob(builder).run();
		file = new File("/home/jonathan/Downloads/ftmpo.mp4");
		

		ClientService clientService = ClientServiceFactory.getInstance();
		Client videoUploadClient = clientService.getClient();

		WebResource webResource = videoUploadClient.resource(DOMAIN);
		webResource.addFilter(new LoggingFilter());

		// 1. INIT the file upload
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			
		params.add("command", "INIT");
		params.add("media_type", "video/mp4");
		params.add("media_category", "amplify_video");

		//File file = new File(filePath);
		int bytes = (int) file.length();
		String totalBytes = Integer.toString(bytes);

		params.add("total_bytes", totalBytes);

		ClientResponse response = webResource.path(RESOURCE)
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, params);

		int status = response.getStatus();

		if (status != 202 && status != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		String output = response.getEntity(String.class);
		System.out.println(output);

		JsonObject jsonObj = new JsonParser().parse(output).getAsJsonObject();
		String mediaId = jsonObj.get("media_id_string").getAsString();

		// 2. APPEND Using the media_id perform the binary upload

		final FormDataMultiPart form = new FormDataMultiPart();

		form.field("command", "APPEND");
		form.field("media_id", mediaId);
		form.field("segment_index", "0");

		final FileDataBodyPart filePart = new FileDataBodyPart("media", file);

		final FormDataMultiPart multiPartForm = (FormDataMultiPart) form.bodyPart(filePart);

		response = webResource.path(RESOURCE)
				.type(MediaType.MULTIPART_FORM_DATA_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, multiPartForm);

		// Return code should be 204 or 200 == OK
		status = response.getStatus();
		if (status != 204 && status != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		// 3. FINALIZE video upload
		
		MultivaluedMap<String, String> finalizeParams = new MultivaluedMapImpl();
		finalizeParams.add("command", "FINALIZE");
		finalizeParams.add("media_id", mediaId);
		
		response = webResource.path(RESOURCE)
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, finalizeParams);
		
		status = response.getStatus();
		if (status != 200 && status != 201) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		output = response.getEntity(String.class);
		System.out.println(output);
		
		// STATUS
		
		/*  
		 * Create a pause of 5 seconds to check on the video upload status
		 * this is a very small video file, adjust according to size 
		 * print a dot for each second to the console.
		 */
		Runnable notifier = new Runnable() {
		    public void run() {
		        System.out.print(".");
		    }
		};
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(notifier, 1, 1, TimeUnit.SECONDS);
		System.out.println("sleeping for 45 secs");
		TimeUnit.SECONDS.sleep(45);
	
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("command", "STATUS");
		queryParams.add("media_id", mediaId);

		response = webResource.path(RESOURCE)
				.queryParams(queryParams)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);
		
		output = response.getEntity(String.class);
		
		
		
		JSONObject mainObject = new JSONObject(output);

		long media_id = Long.parseLong(mainObject.getString("media_id"));
		int percentDone = mainObject.getJSONObject("processing_info").getInt("progress_percent");
		System.out.println(media_id);
		
		if (percentDone == 100) {
	        System.out.println("Media uploaded fully with id: " + output);

			
			Twitter twitter = new TwitterFactory().getInstance();
		       
	        StatusUpdate update = new StatusUpdate(tweetContents);
	        
	        long mediaIds[] = new long[1];
	        mediaIds[0] = media_id;
	        update.setMediaIds(mediaIds);
	        twitter.updateStatus(update);
		}
		
		
		
		// kill the scheduler
		scheduler.shutdownNow();
		
		
		
//		client.setTrace(true);
//		client.setSandbox(false);
		
		// get the first account
		//Account account = client.getAccount(ACCOUNT_ID);
		
		
		
//		Video video = new Video(account);
//		video.setDescription("test video description");
//		video.setTitle("test video title");
//		video.setVideo_media_id(mediaId);
//		video.save();
//		System.out.println("video to string" + video.toString());
		
	}
}
