public class Match {
    private Player homePlayer = new Player();
    private Player visitorPlayer = new Player();
    private String result;
    private int homeScore;
    private int visitorScore;
    private boolean alreadyPlayed = false;

    public Match(){
        this.result = "";
        this.homeScore = -1;
        this.visitorScore = -1;
    }

    public Match(Player homePlayer,Player visitorPlayer){
        this.homePlayer = homePlayer;
        homePlayer.setNumberHomeMatch(homePlayer.getNumberHomeMatch()+1);
        this.visitorPlayer = visitorPlayer;
        visitorPlayer.setNumberAwayMatch(visitorPlayer.getNumberAwayMatch()+1);
        this.result = "";
        this.homeScore = -1;
        this.visitorScore = -1;
    }

    public void display(){
        System.out.println("*** Infos Match ***");
        System.out.println("*" + this.homePlayer.getName() + " " + this.result + " " + this.visitorPlayer.getName() + "*");
    }

    //*** ACCESSEURS ***
    //Retourne l'équipe à domicile
    public Player getHomePlayer() {
        return homePlayer;
    }
    //Retourne l'équipe à l'extérieure
    public Player getVisitorPlayer() {
        return visitorPlayer;
    }
    //Retourne le résultat
    public String getResult() {
        return result;
    }
    //Retourne le score de l'équipe domicile
    public int getHomeScore() {
        return homeScore;
    }
    //Retourne le score de l'équipe extérieur
    public int getVisitorScore() {
        return visitorScore;
    }
    //Retourne l'état du match
    public boolean isAlreadyPlayed(){
        return alreadyPlayed;
    }

    //*** MUTATEURS ***
    //Modifie l'équipe à domicile
    public void setHomePlayer(Player homeTeam) {
        if(this.homePlayer.getPlayerNumber() != 0) this.homePlayer.setNumberHomeMatch(this.homePlayer.getNumberHomeMatch()-1);
        this.homePlayer = homeTeam;
        this.homePlayer.setNumberHomeMatch(this.homePlayer.getNumberHomeMatch()+1);
    }
    //Modifie l'équipe à l'extérieure
    public void setVisitorPlayer(Player visitorTeam) {
        if(this.visitorPlayer.getPlayerNumber() != 0) this.visitorPlayer.setNumberAwayMatch(this.visitorPlayer.getNumberAwayMatch()-1);
        this.visitorPlayer = visitorTeam;
        this.visitorPlayer.setNumberAwayMatch(this.visitorPlayer.getNumberAwayMatch()+1);
    }
    //Modifie le résultat
    public void setResult(String result) {
        if(this.result != ""){

        }
        this.result = result;
        result = result.replaceAll(" ","");

        //Ajoute score domicile & extérieur
        String[] a = result.split("-");
        this.homeScore = Integer.parseInt(a[0]);
        this.visitorScore = Integer.parseInt(a[1]);

        //Ajoute les buts aux joueurs
        this.homePlayer.setGoalsScored(this.homePlayer.getGoalsScored() + this.homeScore);
        this.homePlayer.setGoalsTaken(this.homePlayer.getGoalsTaken() + this.visitorScore);
        this.visitorPlayer.setGoalsScored(this.visitorPlayer.getGoalsScored() + this.visitorScore);
        this.visitorPlayer.setGoalsTaken(this.visitorPlayer.getGoalsTaken() + this.homeScore);

        //Ajoute les points aux joueurs
        if(this.homeScore > this.visitorScore){
            this.homePlayer.setPoints(this.homePlayer.getPoints() + 3);
        }
        else if (this.homeScore < this.visitorScore){
            this.visitorPlayer.setPoints(this.visitorPlayer.getPoints() + 3);
        }
        else{
            this.homePlayer.setPoints(this.homePlayer.getPoints() + 1);
            this.visitorPlayer.setPoints(this.visitorPlayer.getPoints() + 1);
        }

        this.alreadyPlayed = true;
    }
    //Modifie le score de l'équipe domicile
    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }
    //Modifie le score de l'équipe extérieur
    public void setVisitorScore(int visitorScore) {
        this.visitorScore = visitorScore;
    }
    //Modifie l'état du match
    public void setAlreadyPlayed(boolean alreadyPlayed){
        this.alreadyPlayed = alreadyPlayed;
    }
}
