/**
 * 
 */
package Uebung2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author Robert Ullmann
 *
 */
public class Server extends Thread {
	private Socket socketClient = null;
	private ServerSocket serverSocket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private String responseString = "";

	static final String HTML_START = "<html> \n"
			+"<head> \n"
			+ "<title>HTTP POST Server in java</title> \n"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" /> \n"
			+ "</head> \n"
			+ "<body> \n";
	static final String HTML_END = "</body>" + "</html>";
	private String responseStringIndex = Server.HTML_START
			+ "<h2 align=center>Telefonverzeichnis</h2>\n"
			+ "<h3>Sie k\u00f6nnen nach Name oder nach Telefonnummer oder nach beiden (nebenl\u00e4ufig) suchen.</h3>\n"
			+ "<form action=\"./\" "
			+ "method=\"get\" accept-charset=\"UTF-8\">\n"
			+ "<table>\n"
			+ "<tr> <td valign=top>Name:</td> <td><input name=A></td> <td></td> </tr>\n"
			+ "<tr> <td valign=top>Nummer:</td> <td><input name=B></td> <td></td> </tr>\n"
			+ "<tr> <td valign=top><input type=submit value=Suchen></td>\n"
			+ " <td><input type=reset></td></form>\n"
			+ " <tr><td><form action=\"./\" "
			+ "method=\"get\" accept-charset=\"UTF-8\">"
			+ "<input type=submit value=\"Server beenden\" name=D></td> </tr>\n"
			+ "</table>\n" + "</form>\n";

	public Server(Socket client, ServerSocket server) {

		this.socketClient = client;
		this.serverSocket = server;
	}

	public void run() {
		try {
			responseString = "";
			// BufferReader zum lesen des Client Inputs
			System.out.println("The Client " + socketClient.getInetAddress()
					+ ":" + socketClient.getPort() + " is connected");

			// Buffered Reader/Writer zum lesen/schreiben zum Client
			in = new BufferedReader(new InputStreamReader(
					socketClient.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					socketClient.getOutputStream()));

			// Request in String
			String httpString = in.readLine();


			// Request unterteilen
			String[] splitString = httpString.split(" ");
			String httpMethod = splitString[0];
			String httpRequest = splitString[1];
			System.out.println(httpMethod);
			System.out.println(httpRequest);

			// Zeige Request
			if (!httpString.startsWith("GET /favicon.ico")) {
				System.out.println("Server Handels " + httpString
						+ " Request of Client: "
						+ socketClient.getInetAddress());
			}

			if (httpMethod.equals("GET")) {
				if (httpRequest.equals("/?") || httpRequest.equals("/")
						|| httpRequest.equals("/favicon.ico")) {

					// Baue HTML-Suchen-Seite
					responseString = responseStringIndex;
					responseString += Server.HTML_END;
				}

				else if (httpRequest.equals("/?D=Server+beenden")) {
					// Baue HTML-Exit-Seite
					responseString = Server.HTML_START
							+ "<h2 align=center>Server Beendet</h2>\n"
							+ Server.HTML_END;

					System.out.println("Server beendet!");
					ServerMain.exit = true;

					;

				}

				else if (httpString.startsWith("GET /?")) {

					System.out.println(Arrays.toString(splitString));
					String[] getValues = splitString[1].split("\\?");
					String[] splitAnd = getValues[1].split("&");
					String[] firstWord = splitAnd[0].split("=");
					String[] secondWord = splitAnd[1].split("=");
					System.out.println("1:" + Arrays.toString(getValues)
							+ "2:" + Arrays.toString(splitAnd) + "3:"
							+ Arrays.toString(firstWord) + "4:"
							+ Arrays.toString(secondWord));

					// Fehlermeldung bei Leereeingaben
					if ((firstWord.length == 1 || firstWord[1]
							.matches("[+]*"))
							&& (secondWord.length == 1 || secondWord[1]
									.matches("[+]*"))) {
						System.out
								.println("Server Handels Empty Request of Client: "
										+ socketClient.getInetAddress());
						responseString = responseStringIndex;
						responseString += "<table>\n<tr>\n<th>"
								+ "Bitte keine Leereingaben!"
								+ "</th>\n</tr>\n</table>\n"
								+ Server.HTML_END;
					} else {
						// Fuegt die uebergebenen Parameter einem Array
						// hinzu im Falle das sie nicht null und auch nicht
						// aus nur + (html leerstellen) bestehen
						// und codiert sie zurueck in utf 8 um die
						// sonderzeichen nutzen zu koennen
						String[] input = new String[2];
						System.out.println("Server searchs for Client: "
								+ socketClient.getInetAddress());
						input[0] = firstWord.length != 1
								&& firstWord[1].matches("[+]*") == false ? java.net.URLDecoder
								.decode(firstWord[1], "UTF-8") : null;
						input[1] = secondWord.length != 1
								&& secondWord[1].matches("[+]*") == false ? java.net.URLDecoder
								.decode(secondWord[1], "UTF-8") : null;
						System.out.println("Searchrequest: "
								+ (input[0] == null ? "" : input[0]) + " "
								+ (input[1] == null ? "" : input[1]));
						ServerTelefonbuch telefonbuch = new ServerTelefonbuch();
						// Sucht mit Hilfe des Telefonbuchs aus aufgabe 1 in
						// leicht abgewandelter Form
						telefonbuch.Suchen(input);
						responseString = Server.HTML_START
								+ "<h2 align=center>Suchergebnisse</h2>\n"
								+ "\n" + "<form action=\"./\" "
								+ "method=\"get\">\n" + "<table>\n";
						System.out
								.println("Server showing results for Client: "
										+ socketClient.getInetAddress());
						// Baut die Html Seite zum einzeigen der
						// Suchergebnisse
						for (int i = 0; i < telefonbuch.ausgabeName.size(); i++) {
							responseString += "<tr>\n<th>"
									+ telefonbuch.ausgabeName.get(i)
									+ "</th>\n</tr>\n";
						}
						for (int i = 0; i < telefonbuch.ausgabeNummern
								.size(); i++) {
							responseString += "<tr>\n<th>"
									+ telefonbuch.ausgabeNummern.get(i)
									+ "</th>\n</tr>\n";
						}
						responseString += "<td><input type=submit value=\"Zur\u00FCck\"></td>\n</tr>\n"
								+ "</table>\n"
								+ "</form>\n"
								+ Server.HTML_END;

					}

				}

					// // Aufruf ohne Parameter
					// if (httpString.startsWith("GET /favicon")) {
					// System.out.println(" Fav-Request");
					// in.close();
					//
					// }
					// else if
					// (httpString.startsWith("GET /")||httpString.equals("GET /?")||httpString.equals("GET /favicon.ico"))
					// {

					// Wenn Get-Post kommt

					
				}
			sendResponse(responseString);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void sendResponse(String responseString) throws IOException {
		out.write("HTTP/1.0 200 OK\r\n");
		out.write("Content-Type: text/html\r\n");
		out.write("\r\n");
		out.write(responseString);
		out.flush();
		out.close();

	}

}
