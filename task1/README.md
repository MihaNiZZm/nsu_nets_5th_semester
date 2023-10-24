# UDP multicast app usage guide.

1. Clone the repository and build both projects using IntellijIdea or any other IDE.
2. Run "muticastServer" app. Use command line arguments to configure your run:
1st argument - IP address of a multicast group you want to listen to
2nd argument - port you want to listen on
3. Run "UdpMulticastApp" app. Use command line arguments to configure your run:
1st argument - IP address of a multicast group you want to join (should be equal to 1st argument of a server app)
2nd argument - port that is being listened by server app (should be equal to 2nd argument of a server app)
4. Now you can track active users in your multicast group using sever app.
