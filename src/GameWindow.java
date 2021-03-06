import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends JFrame {
    private JTable rankingTable;
    private JTable calendarTable;
    private JButton validButton = new JButton("Valider");

    private Calendar calendar = new Calendar();
    private Ranking ranking = new Ranking();

    public GameWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("TOURNOI FIFA");
        this.setSize(1000, 400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //Création du Tableau d'affichage du classement
        Object[][] data = {};
        String[] titleRanking = {"Joueur","Equipe","Pts","DB","BP","BC"};
        this.rankingTable = new JTable(new DefaultTableModel(data, titleRanking)) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.rankingTable.setFillsViewportHeight(true);
        this.rankingTable.setRowHeight(20);
        this.rankingTable.getColumn("Joueur").setMaxWidth(90);
        this.rankingTable.getColumn("Equipe").setMaxWidth(90);
        this.rankingTable.getColumn("Pts").setMaxWidth(40);
        this.rankingTable.getColumn("DB").setMaxWidth(40);
        this.rankingTable.getColumn("BP").setMaxWidth(40);
        this.rankingTable.getColumn("BC").setMaxWidth(40);
        JScrollPane scRanking = new JScrollPane(this.rankingTable);
        scRanking.setPreferredSize(new Dimension(340,Param.NB_PLAYER*this.rankingTable.getRowHeight()+23));

        data = initDataCalendarTable();
        String[] titleCalendar = {"Equipe Dom","Score","Equipe Ext"};

        //Création du Tableau d'affichage du calendrier
        this.calendarTable = new JTable(data, titleCalendar) {
            private Border weekBorder = new MatteBorder(2, 0, 0, 0, Color.BLUE);
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);
                JComponent jc = (JComponent)c;
                jc.setBorder(row % Param.NB_TV == 0 ? weekBorder : null);
                return c;
            }
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.calendarTable.setFillsViewportHeight(true);
        this.calendarTable.setRowHeight(18);
        this.calendarTable.getColumn("Equipe Dom").setMaxWidth(100);
        this.calendarTable.getColumn("Score").setMaxWidth(70);
        this.calendarTable.getColumn("Equipe Ext").setMaxWidth(100);
        JScrollPane scCalendar = new JScrollPane(this.calendarTable);
        scCalendar.setPreferredSize(new Dimension(270,300));

        JPanel panelCalendar = new JPanel();
        panelCalendar.setMaximumSize(new Dimension(100,100));
        panelCalendar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panelCalendar.add(scCalendar);
        panelCalendar.add(this.validButton);
        JPanel panelRanking = new JPanel();
        panelRanking.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panelRanking.add(scRanking);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(panelCalendar);
        mainPanel.add(panelRanking);
        this.getContentPane().add(mainPanel);
        this.setVisible(true);

        //initialise le tableau de classement
        updateRankingTable();

        //Affiche le nombre de matchs à domicile et à l'exterieur
        /*for(Player player : Param.PLAYERS){
            System.out.println(player.getName() + " - home : " + player.getNumberHomeMatch() + " - away : " + player.getNumberAwayMatch());
        }*/

        //Fonction déclanché par le validButton
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Match match = new Match();
                String homeTeam = "home";
                String visitorTeam = "visitor";
                if(calendarTable.getSelectedRow() == -1){
                    match = calendar.getCurrentMatch();
                    homeTeam = match.getHomePlayer().getName();
                    visitorTeam = match.getVisitorPlayer().getName();
                }
                else{
                    homeTeam = calendarTable.getValueAt(calendarTable.getSelectedRow(),0).toString();
                    visitorTeam = calendarTable.getValueAt(calendarTable.getSelectedRow(),2).toString();
                    match = calendar.getMatchWith(Param.getPlayerByName(homeTeam),Param.getPlayerByName(visitorTeam));
                }
                calendarTable.clearSelection();
                ScoreWindow scoreWindow = new ScoreWindow(null,"Score Match",true, homeTeam, visitorTeam);
                if(scoreWindow.sendData()){
                    String score = scoreWindow.getScore();
                    match.setResult(score);
                    addScoreToTable(match,score);
                    ranking.updateRanking();
                    updateRankingTable();
                }
            }
        };

        //liaison entre le bouton et sa fonction
        this.validButton.addActionListener(action);
    }

    //Initialise le tableau de calendrier
    public Object[][] initDataCalendarTable() {
        Object[][] data = new Object[Param.NB_MATCH][2];
        String homeTeam;
        String visitorTeam;
        int compteur = 0;
        for (Week week : this.calendar.getWeeks()) {
            for (Match match : week.getMatchs()) {
                homeTeam = match.getHomePlayer().getName();
                visitorTeam = match.getVisitorPlayer().getName();
                data[compteur] = new Object[]{homeTeam, "", visitorTeam};
                compteur++;
            }
        }
        return data;
    }

    //Initialisation du Calendrier
    public void initCalendarFromAlgo() {
        /*int cursor = 0;

        List<Pair> allHomeMatchs = new ArrayList<>();
        for (int team1 = 1; team1 <= Param.PLAYERS.size(); team1++) {
            cursor = 0;
            for (int team2 = team1 + 1; team2 <= Param.PLAYERS.size(); team2++) {
                if (team1 != team2) {
                    if (cursor == Param.PLAYERS.size() / 2) {
                        allHomeMatchs.add(new Pair(team2, team1));
                    } else {
                        allHomeMatchs.add(new Pair(team1, team2));
                        cursor++;
                    }
                }
            }
        }

        int numberHomeMatchs = (allHomeMatchs.size() / 2);

        while (this.calendar.size() != numberHomeMatchs) {
            this.calendar.add(new MatchWeek());
            //System.out.println("Taille calendrier : " + this.calendar.size());
            for (Pair pair1 : new ArrayList<Pair>(allHomeMatchs)) {
                Match match1 = new Match(getPlayerByNumber((Integer) pair1.getKey()), getPlayerByNumber((Integer) pair1.getValue()));
                for (Pair pair2 : new ArrayList<Pair>(allHomeMatchs)) {
                    Match match2 = new Match(getPlayerByNumber((Integer) pair2.getKey()), getPlayerByNumber((Integer) pair2.getValue()));
                    //!!!CONDITIONS!!! 4 Equipes différentes &&
                    if ((match1.getHomePlayer() != match2.getHomePlayer()) && (match1.getHomePlayer() != match2.getVisitorPlayer()) &&
                            (match1.getVisitorPlayer() != match2.getHomePlayer()) && (match1.getVisitorPlayer() != match2.getVisitorPlayer()) &&
                            (this.calendar.get(this.calendar.size() - 1).getWeek().isEmpty())) {
                        List week = new ArrayList<>();
                        week.add(match1);
                        week.add(match2);
                        List<Player> waiters = new ArrayList<>(getWaiters(week));
                        if (this.calendar.size() < 2) {
                            this.calendar.get(this.calendar.size() - 1).setWeek(week);

                            this.calendar.get(this.calendar.size() - 1).setWaitingPlayer(waiters);

                            allHomeMatchs.remove(pair1);
                            allHomeMatchs.remove(pair2);
                        } else {
                            //int allWaitersMustPlay = 0;
                            int allMandatoryWaitersWait = 0;
                            List<Player> mandatoryWaiters = new ArrayList<>(getMandatoryWaiters());
                            List<Player> possibleWaiters = new ArrayList<>(getPossibleWaiters(mandatoryWaiters));

                            //Vérifie si les joueurs devant attendre, attendent
                            for (Player mandatoryWaitersPlayer : mandatoryWaiters) {
                                for (Player waitersPlayer : new ArrayList<>(waiters)) {
                                    if (mandatoryWaitersPlayer.equals(waitersPlayer)) {
                                        allMandatoryWaitersWait++;
                                        waiters.remove(waitersPlayer);
                                    }
                                }
                            }

                            if (allMandatoryWaitersWait == mandatoryWaiters.size()) {
                                for (Player waitersPlayer : new ArrayList<>(waiters)) {
                                    for (Player possibleWaitersPlayer : possibleWaiters) {
                                        if (waitersPlayer.equals(possibleWaitersPlayer)) waiters.remove(waitersPlayer);
                                    }
                                }
                            }

                            if (waiters.isEmpty()) {
                                waiters = getWaiters(week);
                                this.calendar.get(this.calendar.size() - 1).setWeek(week);

                                this.calendar.get(this.calendar.size() - 1).setWaitingPlayer(waiters);

                                allHomeMatchs.remove(pair1);
                                allHomeMatchs.remove(pair2);
                                /*
                                System.out.print("Mandatory Waiters : ");
                                for(Player player : mandatoryWaiters){
                                    System.out.print(player.getPlayerNumber() + " ");
                                }
                                System.out.println();
                                System.out.print("Possible Waiters : ");
                                for(Player player : possibleWaiters){
                                    System.out.print(player.getPlayerNumber() + " ");
                                }
                                System.out.println();

                            }
                                /*for(Player player : possibleWaiters){
                                    if((player.getPlayerNumber() != (int) match1.getKey()) && (player.getPlayerNumber() != (int) match1.getValue()) &&
                                            (player.getPlayerNumber() != (int) match2.getKey()) && (player.getPlayerNumber() != (int) match2.getValue())) allWaitersMustPlay++;
                                }

                                if(allWaitersMustPlay == (players.size() - (this.nbTV*2))){
                                    week.add(new Match(getPlayerByNumber((Integer) match1.getKey()),getPlayerByNumber((Integer) match1.getValue())));
                                    week.add(new Match(getPlayerByNumber((Integer) match2.getKey()),getPlayerByNumber((Integer) match2.getValue())));
                                    this.calendar.get(this.calendar.size()-1).setWeek(week);

                                    this.calendar.get(this.calendar.size()-1).setWaitingPlayer(getWaiters(week));

                                    allHomeMatchs.remove(match1);
                                    allHomeMatchs.remove(match2);
                            }
                        }
                    }
                }
            }
        }
        //Ajout des derniers matchs Aller dans le calendrier si les matchs aller restant ne remplisse pas un week
        if (allHomeMatchs.size() >= this.nbTV){
            System.out.println("Erreur dans la création du calendrier aller");
        }
        else if(allHomeMatchs.size() > 0){
            this.calendar.add(new MatchWeek());
            List week = new ArrayList<>();
            for(Pair pair : new ArrayList<>(allHomeMatchs)){
                week.add(new Match(getPlayerByNumber((Integer) pair.getKey()),getPlayerByNumber((Integer) pair.getValue())));
                allHomeMatchs.remove(pair);
            }
            this.calendar.get(this.calendar.size()-1).setWeek(week);
        }

        for(Player player : this.players){
            System.out.println("Player : " + player.getPlayerNumber());
            System.out.println("nombre de matche a domicile : " + player.getNumberHomeMatch());
            System.out.println("nombre de matche a l'exterieur : " + player.getNumberAwayMatch());
        }

        int day = 0;
        for (MatchWeek week : this.calendar) {
            day++;
            System.out.println();
            System.out.println("*** Journée " + day + " ***");
            for (Match match : week.getWeek()) {
                System.out.println(match.getHomePlayer().getPlayerNumber() + " - " + match.getVisitorPlayer().getPlayerNumber());
            }
            System.out.println("Waiters :");
            for (Player player : week.getWaitingPlayer()) {
                System.out.print(player.getPlayerNumber() + " ");
            }
            System.out.println();
        }
        System.out.println(allHomeMatchs);*/
    }

    //Retourne les waiters possible
    public void getPossibleWaiters(List<Player> mandatoryWaiters) {
/*
        int minimum = 1000;
        List<Player> possibleWaiters = new ArrayList<>();
        List<Player> previousWaiters = new ArrayList<>(this.calendar.getWeeks(this.calendar.size() - 2).getWaitingPlayer());

        for (Player player : possibleWaiters) {
            if (player.getNumberWaiting() < minimum) minimum = player.getNumberWaiting();
        }

        while ((possibleWaiters.size() + mandatoryWaiters.size()) < Param.PLAYERS.size() - (Param.NB_TV * 2)) {
            possibleWaiters = new ArrayList<>(Param.PLAYERS);
            for (Player previousWaiter : previousWaiters) {
                possibleWaiters.remove(previousWaiter);
            }

            for (Player previousWaiter : mandatoryWaiters) {
                possibleWaiters.remove(previousWaiter);
            }

            //System.out.println("mini : " + minimum);
            for (Player player : new ArrayList<>(possibleWaiters)) {
                //System.out.println(player.getPlayerNumber());
                if (player.getNumberWaiting() > minimum) possibleWaiters.remove(player);
            }
            minimum++;
        }
        return possibleWaiters;*/
    }

    public List<Player> getMandatoryWaiters() {
        int minimum = 1000;
        List<Player> mandatoryWaiters = new ArrayList<>(Param.PLAYERS);

        for (Player player : mandatoryWaiters) {
            if (player.getNumberWaiting() < minimum) minimum = player.getNumberWaiting();
        }

        for (Player player : new ArrayList<>(mandatoryWaiters)) {
            if (player.getNumberWaiting() > minimum) mandatoryWaiters.remove(player);
        }

        if (mandatoryWaiters.size() > Param.PLAYERS.size() - (Param.NB_TV * 2)) mandatoryWaiters = new ArrayList<>();

        return mandatoryWaiters;
    }

    public void addScoreToTable(Match match, String score){
        int row = findRowByMatch(match);
        this.calendarTable.setValueAt(score,row,1);
    }

    public void updateRankingTable(){
        DefaultTableModel model = (DefaultTableModel) this.rankingTable.getModel();
        int rowCount = model.getRowCount();
        for(int row=rowCount-1; row>=0; row--){
            model.removeRow(row);
        }
        for(Pair pair : this.ranking.getRanking()) {
            Player player = (Player)pair.getValue();
            model.addRow(new Object[]{player.getName(),player.getTeam(),player.getPoints(),player.getGoalDifference(),player.getGoalsScored(),player.getGoalsTaken()});
        }
    }

    private int findRowByMatch(Match match1){
        int row = 0;
        for (Week week : this.calendar.getWeeks()){
            for (Match match2 : week.getMatchs()){
                if(match1 == match2) return row;
                row++;
            }
        }
        return row;
    }
}

