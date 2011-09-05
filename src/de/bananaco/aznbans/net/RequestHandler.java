package de.bananaco.aznbans.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import de.bananaco.aznbans.AznBans;

public final class RequestHandler extends Thread {
	/**
	 * The socket we are using to obtain a request.
	 */
	private final Socket socket;
	
	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Listens for a request.
	 */
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = reader.readLine();
			// Read the request and handle it.
			this.handleRequest(socket, line);

			// Finally close the socket.
			socket.close();
		} catch (IOException ignored) {
		} catch (Exception ex) {
		ex.printStackTrace();
		}
	}

	private void handleRequest(Socket socket, String request) throws IOException {
		if (request == null) {
			return;
		}
		// Send the response.

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		if(request.contains("QUERY"))
		out.writeBytes(AznBans.getBanHandler().getAllBans());
	
		out.close();
		}
	
}