package com.ankama;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class CombatGroup implements Runnable {

    private final int baseHealthPoints = 50;
    private final int strongAttackDamage = 20;
    private final int lightAttackDamage = 10;
    private final double strongAttackOdds = .5;
    private final double lightAttackOdds = .9;

    private ArrayList<IPlayer> players = new ArrayList<>();
    private IPlayer lastEmitter;

    private LinkedBlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();

    private int turnIndex;
    private Boolean isGameOver = false;

    public CombatGroup(ClientServerHandler handler1, ClientServerHandler handler2) {
        handler1.initCombatPlayer(baseHealthPoints, handler2.getClientPseudo());
        players.add(handler1.getPlayer());
        handler2.initCombatPlayer(baseHealthPoints, handler1.getClientPseudo());
        players.add(handler2.getPlayer());
        turnIndex = (Math.random() <= 0.5) ? 0 : 1;
    }

    public CombatGroup(ClientServerHandler handler) {
        handler.initCombatPlayer(baseHealthPoints, "AI");
        players.add(handler.getPlayer());
        players.add(new RobotPlayer(baseHealthPoints));
        turnIndex = (Math.random() <= 0.5) ? 0 : 1;
    }

    public void newEvent(String choice, IPlayer player) {
        lastEmitter = player;
        eventQueue.add(choice);
    }

    private void togglePlayersInputs() {
        if (players.get(turnIndex) instanceof HumanPlayer) {
            ((HumanPlayer) players.get(turnIndex)).getHandler().toggleClientTurn(true);
        }
        if (getOpponent(players.get(turnIndex)) instanceof HumanPlayer) {
            ((HumanPlayer) getOpponent(players.get(turnIndex))).getHandler().toggleClientTurn(false);
        }
    }

    private void playNewTurn() {
        togglePlayersInputs();
        if (players.get(turnIndex) instanceof RobotPlayer) {
            robotPlayComputing();
        } else {
            ((HumanPlayer) players.get(turnIndex)).displayActionsAvailable();
        }
    }

    @Override
    public void run() {
        playNewTurn();
        String eventString;
        while (!isGameOver) {
            try {
                eventString = eventQueue.take();
                switch (eventString) {
                    case "DISCONNECTED":
                        computeForfeit();
                        break;
                    case "1":
                    case "2":
                        computePlayerChoice(eventString);
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void computePlayerChoice(String choice) {
        IPlayer attacker = lastEmitter;
        IPlayer target = getOpponent(lastEmitter);
        if (choice.equals("1")) {
            tryAttack(strongAttackDamage, strongAttackOdds, attacker, target);
        } else if (choice.equals("2")) {
            tryAttack(lightAttackDamage, lightAttackOdds, attacker, target);
        }
    }

    public void tryAttack(int damage, double odds, IPlayer attacker, IPlayer target) {
        if (Math.random() <= odds) {
            target.hit(damage);
            if (attacker instanceof HumanPlayer) {
                ((HumanPlayer) attacker).displayHit(target.getPseudo(), damage, target.getHealthPoints());
            }
            if (target instanceof HumanPlayer) {
                ((HumanPlayer) target).displayDamaged(attacker.getPseudo(), damage);
            }
        } else {
            if (attacker instanceof HumanPlayer) {
                ((HumanPlayer) attacker).displayMiss();
            }
            if (target instanceof HumanPlayer) {
                ((HumanPlayer) target).displayDodge(attacker.getPseudo());
                ;
            }
        }

        if (target.getHealthPoints() <= 0) {
            setGameOver(attacker, target);
        } else {
            switchTurn();
        }
    }

    public IPlayer getOpponent(IPlayer currentPlayer) {
        if (players.get(0).equals(currentPlayer))
            return players.get(1);
        else
            return players.get(0);
    }

    private void computeForfeit() {
        IPlayer winner = getOpponent(lastEmitter);
        if (winner instanceof HumanPlayer) {
            if (lastEmitter instanceof HumanPlayer) {
                ((HumanPlayer) winner).computeWin(((HumanPlayer) lastEmitter).getHandler().getClientPseudo());
            }
        }
        isGameOver = true;
    }

    private void switchTurn() {
        if (turnIndex == 0) {
            turnIndex = 1;
        } else
            turnIndex = 0;

        playNewTurn();
    }

    private void setGameOver(IPlayer winner, IPlayer loser) {
        String winnerPseudo;
        String loserPseudo;
        if (winner instanceof HumanPlayer) {
            loserPseudo = loser.getPseudo();
            ((HumanPlayer) winner).computeWin(loserPseudo);
            ((HumanPlayer) winner).getHandler().closeCommunication();
        }
        if (loser instanceof HumanPlayer) {
            winnerPseudo = winner.getPseudo();
            ((HumanPlayer) loser).computeLoss(winnerPseudo);
            ((HumanPlayer) loser).getHandler().closeCommunication();
        }

        isGameOver = true;
    }

    private void robotPlayComputing() {
        String aiChoice = ((RobotPlayer) players.get(turnIndex)).makeAIChoice();
        newEvent(aiChoice, players.get(turnIndex));
    }
}
