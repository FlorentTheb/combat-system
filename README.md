# Combat system
###### This project is intended for a technical test and is running locally on a machine

### HOW TO INSTALL AND USE
*You will need at least **java 17** to execute the programs*
1. Clone this repo where you wish
2. Open a command prompt at the root of the repo *(where **pom.xml** is located)*
3. To open the server :
> java -jar server.jar
4. To open a client :
> java -jar client.jar
5. If you need or want to generate your own .jar, first get **Maven *(V3.X)*** on your machine
6. On a command prompt where pom.xml is located :
> mvn clean package
7. A client.jar and a server.jar should be cleanly generated at the same location, follow the previous step then.

<br />

### HOW TO PLAY
1. Start the server.jar
2. Chose a pseudo
3. Chose a gamemode

#### Mode Player vs Player
1. If you are the first one, you will be waiting for another Client chosing this gamemode as well
2. When 2 players have joined the waiting room, they form a group and face each other
3. One of the 2 players is chosen to start, while the other waits for its turn (for the opponent to finish its turn)
4. As a player, you have 2 choices :
    * Try a powerful strike, having 50% chance of dealing 20 damage
    * Be safe by attacking with a light hit, having 90% chance of dealing 10 damage
5. You will see in the terminal the result of your choice and the next Player will play its turn
6. Each player have 50 health points, so be strategic. First one to 0 will lose, while the other will win !

</br>

#### Mode Player vs IA
1. There is no waiting room, you will face a robot chosing randomly its actions
2. The same principle in this gamemode as the *Player vs Player* mode, your opponent is just a robot. Easy right ?
3. No seriously, don't lose against a bot... Humanity's pride depends on your choices !

</br>

### TECHNICAL AREA
###### If you are not familiar with code, not much a need to look any further. Oh and the next part is in french since also.

#### Stack technique :
Java 17 | Maven 3.x | JUnit 5 | Mockito 5 | Java Sockets

#### Tests unitiaires :
Des tests unitaires sur des fonctionnalités ciblées sont écrits et peuvent être lancés manuellement :
> mvn test

#### Principes :

La communication Serveur <-> Clients a été établi en Java sockets

1. Un serveur ouvre un certain port et écoute les entrées sur ce dernier (ServerSocket)
2. Un client se connecte sur ce même port (Socket)
3. Le serveur détecte la connexion et créé un "agent" qui sera responsable d'écouter le client et de lui transmettre des informations
4. Cet agent implémente ***Runnable*** afin de pouvoir lancer sa boucle d'écoute depuis un thread secondaire du serveur, le principale restant à l'écoute de connexions d'autres clients
5. L'agent que l'on appelera *Handler*, appelé dans ce projet *ClientServerHandler*, permet donc de transmettre les informations d'un combat à son client, et de transmettre à un autre Handler de son groupe les choix de son client.
6. Le client communique de la même façon, en écoutant l'entrée clavier de l'utilisateur dans une boucle infini, dans son thread principal, alors qu'un thread est créé pour l'écoute de son Handler et afficher dans le terminal toute information

#### Salle d'attente :
1. Un singleton fait office de salle d'attente : à chaque fois qu'un handler reçoit de son client l'envi d'aller en mode *Player versus Player*, il dit à la salle d'attente de s'ajouter soit même à sa liste (représentant donc tous les joueurs en attente)
2. Au moment de l'ajout dans la salle d'attente, cette dernière dit au Handler d'avertir son client qu'il est en attente d'un autre joueur, dans le cas où il est le 2ème arrivé, elle demande à prévenir son client qu'il rentre en combat, et contre qui (le pseudo de l'adversaire, le 1er étant arrivé dans la salle d'attente).
3. Un thread qui sera représenté par un objet (encore une fois, implémentant ***Runnable***) CombatGroup est alors créé en prenant les deux handlers choisi par la salle d'attente afin de créer le "Joueur" de chacun. On définit aussi ce groupe en référence dans chaque handler (cf partie Système de combat).
4. La salle d'attente retire alors ces deux handlers de sa liste (permet donc de créer d'autres groupes de combat si d'autres clients venaient à jouer en JcJ).
```
if (waitingRoomClients.size() >= 2) {
    CombatGroup combatGroup = new CombatGroup(waitingRoomClients.get(0), waitingRoomClients.get(1));
    waitingRoomClients.get(0).setGroup(combatGroup);
    waitingRoomClients.get(1).setGroup(combatGroup);
    Thread thread = new Thread(combatGroup);
    thread.start();
    waitingRoomClients.remove(0);
    waitingRoomClients.remove(0);
} else {
    client.sendMessageToClient("Waiting for another player to join");
}
```
#### Système de combat
1. Le CombatGroup que l'on va appelé "groupe" représente 2 joueurs.
2. A sa création depuis la salle d'attente, il va définir aléatoirement un des deux joueurs comme étant le 1er à jouer
3. Sa boucle de jeu consiste alors ainsi :
    * Si le joueur qui doit jouer est une IA, procède à un choix aléatoire
    * Sinon, il transmet les choix possibles. Ce dernier transmet les choix à son client
    * Le client fait un choix au handler, qui le transmet au groupe. Subtilité : le groupe est forcément en attente d'un retour de choix, car une LinkedBlockingQueue est implémenté afin de déclenché la suite de la boucle de jeu uniquement sur reception 
    
```
@Override
    public void run() {
        playNewTurn();
        String eventString;
        while (!isGameOver) {
            try {
                eventString = eventQueue.take(); // <-- Bloquant tant que rien n'est ajouté à la queue
                switch (eventString) {
```
Si IA joue :
```
private void robotPlayComputing() {
        String aiChoice = ((RobotPlayer) players.get(turnIndex)).makeAIChoice();
        newEvent(aiChoice, players.get(turnIndex));
    }
```
Si un humain joue :
```
while (socket.isConnected()) {
    msgFromClient = bufferedReader.readLine();
    if (msgFromClient == null || msgFromClient.equals("Bye")) {
        closeCommunication();
        return;
    }

    if (group != null) {
        if (!msgFromClient.equals("1") && !msgFromClient.equals("2")) {
            sendMessageToClient("Unknown attack, try again");
            sendMessageToClient("TOGGLE_INPUTS_ON");
        } else {
            group.newEvent(msgFromClient, player); // <-- Envoie le choix dans la queue
        }
    }
}
```

Qui déclenche :
```
public void newEvent(String choice, IPlayer player) {
    lastEmitter = player;
    eventQueue.add(choice);
}
```

Les joueurs sont représentés par une interface permettant avec un "contrat" de forcer l'implémentation de méthodes communes, que le joueur soit un robot ou un humain
```
public interface IPlayer {
    int getHealthPoints();
    void hit(int damageAmount);
    String getPseudo();
}
```
On peut alors moduler la construction du groupe selon comment il a été instancié pour le mode de jeu
```
public CombatGroup(ClientServerHandler handler1, ClientServerHandler handler2) { // Constructeur Player vs Player 
    handler1.initCombatPlayer(baseHealthPoints, handler2.getClientPseudo());
    players.add(handler1.getPlayer());
    handler2.initCombatPlayer(baseHealthPoints, handler1.getClientPseudo());
    players.add(handler2.getPlayer());
    turnIndex = (Math.random() <= 0.5) ? 0 : 1;
}

public CombatGroup(ClientServerHandler handler) { // Constructeur Player vs IA
    handler.initCombatPlayer(baseHealthPoints, "AI");
    players.add(handler.getPlayer());
    players.add(new RobotPlayer(baseHealthPoints));
    turnIndex = (Math.random() <= 0.5) ? 0 : 1;
}
```
Car un HumanPlayer comme un RobotPlayer *implémente* IPlayer

Lors d'une action, on enlève ou non les points de vie de la cible.

Selon les points de vie finaux post-action de la cible, on continue le prochain tour ou la partie prend fin.

Sur une fin de partie, chaque joueur possède un message s'il a gagné ou perdu.

/!\ Point important : le message de victoire est envoyé automatiquement à un joueur si le deuxième se déconnecte pendant la partie selon ce principe :
1. Un joueur se déconnecte : avant de mettre fin à son programme, le handler en informe son groupe
2. Le groupe reçoit l'information de déconnexion et informe son adversaire qu'il a gagné par forfait
3. Le handler adverse transmet cette information à son client

</br>

Points d'amélioration notés :
* Meilleur affichage : le délai entre chaque tour n'est pas effectué exactement au même moment entre les joueurs
* Bloquage d'affichage : parfois il faut faire "entrée" pour voir la suite des informations (comme les actions possibles), à reproduire..
* Pouvoir faire une confirmation dans la salle d'attente, et implémenter un chat dans cette dernière
* Tests d'intégration, cycle CI/CD jenkins etc si besoin de tester la partie connexion des sockets (dans le cas d'un projet concret)
