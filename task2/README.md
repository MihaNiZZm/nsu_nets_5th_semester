# TCP file sender app usage guide.

1. Clone the repository and build both projects using IntellijIdea or any other IDE.
2. Run "TcpFileServer" app. Use command line to configure your run:
1st argument - port that FileServer will listen on
3. Run "TcpFileClient" app. Use command line to configure your run:
1st argument - IP address of the server (you can find it using "ipconfig" command in cmd for Windows and "ip -a" command in Linux terminal)
2nd argument - port that the server is listening on (should be equal to 1st argument of the server app)
3rd argument - absolute path of a file you want to send to the server.
4. Now you can see the server is receiving file from a client. Server serves some helpful info about receiving (current speed, average speed, progress)
