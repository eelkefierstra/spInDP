package com.nhl.spindp.netcon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.nhl.spindp.Main;
import com.nhl.spindp.Utils;
//import com.sun.net.httpserver.*;

import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class WebSocket
{
	private HttpServer server;
	private Thread worker;
	
	public WebSocket()
	{
		SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();
		server = ServerBootstrap.bootstrap()
				.setListenerPort(8000)
                .setServerInfo("Test/1.1")
                .setSocketConfig(socketConfig)
                .setSslContext(null)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("*", new MyHandler())
                .create();
	}
	
	public WebSocket(int port) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException
	{
		SSLContext sslcontext = null;
        if (port == 8443) {
            // Initialize SSL context
            URL url = Main.class.getResource("/my.keystore");
            if (url == null) {
                System.out.println("Keystore not found");
                System.exit(1);
            }
            sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(url, "secret".toCharArray(), "secret".toCharArray())
                    .build();
        }
		SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();
		server = ServerBootstrap.bootstrap()
				.setListenerPort(port)
                .setServerInfo("Test/1.1")
                .setSocketConfig(socketConfig)
                .setSslContext(sslcontext)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("*", new MyHandler())
                .create();
	}
	
	/*
	public WebSocket() throws IOException
	{
		server = HttpServer.create(new InetSocketAddress(80), 0);
		server.createContext("/test", new MyHandler());
		server.setExecutor(null);
	}
	
	public WebSocket(int port) throws IOException
	{
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/index", new MyHandler());
		server.setExecutor(null);
	}
	*/
	public void start()
	{
		worker = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					init();
				}
				catch (Exception ex) { }
			}
		};
		worker.setDaemon(true);
		worker.setName("WebWorker");
		worker.start();
	}
	
	private void init() throws IOException, InterruptedException
	{
		server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
	}
	
	public void stop() throws IOException, InterruptedException
	{
		server.shutdown(5, TimeUnit.SECONDS);
	}
	
	static class MyHandler implements HttpRequestHandler
	{

		@Override
		public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
		{
			System.out.println(""); // empty line before each request
	        System.out.println(request.getRequestLine());
	        System.out.println("-------- HEADERS --------");
	        for(Header header: request.getAllHeaders())
	        {
	            System.out.println(header.getName() + " : " + header.getValue());
	        }
	        System.out.println("--------");

	        HttpEntity entity = null;
	        if (request instanceof HttpEntityEnclosingRequest)
	            entity = ((HttpEntityEnclosingRequest)request).getEntity();

	        // For some reason, just putting the incoming entity into
	        // the response will not work. We have to buffer the message.
	        String data = "";
	        if (entity != null) {
	            data = EntityUtils.toString(entity);
	        }

	        System.out.println(data);
	        /*
	        File file = new File(Main.class.getResource("/www/index.html").getPath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			reader.close();
	        */
	        HttpEntity responseEntity;
	        RequestLine requestLine = request.getRequestLine();
	        if (requestLine.getMethod().compareTo("GET") == 0)
	        {
	        	File resource = new File(Main.class.getResource("/www" + requestLine.getUri()).getPath());
				if (!resource.isFile())
				{
					response.setStatusCode(HttpStatus.SC_NOT_FOUND);
					responseEntity = new StringEntity(
	                        "<html><body><h1>File" + requestLine.getUri() +
	                        " not found</h1></body></html>",
	                        ContentType.create("text/html", "UTF-8"));
				}
				else if (!resource.canRead())
				{
					response.setStatusCode(HttpStatus.SC_FORBIDDEN);
					responseEntity = new StringEntity(
	                        "<html><body><h1>File" + requestLine.getUri() +
	                        " forbidden</h1></body></html>",
	                        ContentType.create("text/html", "UTF-8"));
				}
				else
				{
					InputStream resourceStream = Main.class.getResourceAsStream("/www" + requestLine.getUri());
					responseEntity = new InputStreamEntity(resourceStream);
				}
				response.setEntity(responseEntity);
	        }
	        else if (requestLine.getMethod().compareTo("POST") == 0)
	        {
	        	int id;
	        	double forward, right;
	        	String[] arr = data.split("&");
	        	id = Integer.valueOf(arr[0].substring(arr[0].lastIndexOf("=")+1));
	        	forward = Utils.map(Double.valueOf(arr[1].substring(arr[1].lastIndexOf("=")+1)), 0.0, 1023.0, -1.0, 1.0);
	        	right = Utils.map(Double.valueOf(arr[2].substring(arr[2].lastIndexOf("=")+1)), 0.0, 1023.0, -1.0, 1.0);
	        	Main.getInstance().setDirection(id, forward, right);
	        	response.setEntity(new StringEntity(data));
	        }
	        else
	        {
	        	response.setEntity(new StringEntity("dummy response"));
	        }
		}
		/*
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			File file = new File(Main.class.getResource("/www/index.html").getPath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			reader.close();
			String response = builder.toString();
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(response.getBytes());
			outputStream.flush();
			outputStream.close();
		}
		*/
	}
	
	static class StdErrorExceptionLogger implements ExceptionLogger {

        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                System.err.println("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                System.err.println(ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }

    }
}
