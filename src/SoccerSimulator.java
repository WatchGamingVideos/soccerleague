import java.util.*;

public class SoccerSimulator {
    // Must be a positive, even number
    public static final int NUM_TEAMS = 20;

    // The number of times each team plays any other team
    public static final int NUM_OCCURRENCES = 2;

    // Raise to increase scoring, lower to decrease scoring
    public static final int SCORING = 70;

    // Raise to increase gap between teams, lower to decrease gap
    // Ideally scoring and variation will add up to 100 or slightly over
    public static final int VARIATION = 30;

    public static void main (String [] args) {
        String input = "";
        Scanner in = new Scanner (System.in);

        while (true) {
            System.out.println ("Welcome to Soccer Simulator\n");

            System.out.println ("(1) Start New Game");
            System.out.println ("(2) Load Existing Save");
            System.out.println ("(3) Exit");
            System.out.print ("Please choose an option: ");
            input = in.nextLine();

            switch (input) {
                case "1":
                    newGame();
                    break;
                case "2":
                    loadFile();
                    break;
                case "3":
                    System.exit(0);
            }
        }
    }

    public static void newGame () {
        Club [] clubs = new Club [NUM_TEAMS];
        generateTeams (clubs);

        // [] - Weeks in a season
        // [][] - Number of games per week
        // [][][] - Number of teams per game
        int [][][] fixtures = new int [(NUM_TEAMS - 1) * NUM_OCCURRENCES][NUM_TEAMS / 2][2];
        generateFixtures(fixtures);

        int currentWeek = 0;
        int currentSeason = 1;

        sim (clubs, currentWeek, fixtures, currentSeason);
    }

    public static void generateTeams (Club [] clubs) {
        for (int i = 0; i < NUM_TEAMS; i ++) {
            clubs [i] = new Club (i, VARIATION);
        }
    }

    public static void generateFixtures (int [][][] fixtures) {
        int firstTeam = 0;
        int [] teamRotation = new int [NUM_TEAMS - 1];

        for (int i = 0; i < teamRotation.length; i ++) {
            teamRotation [i] = i + 1;
        }

        for (int i = 0; i < (NUM_TEAMS - 1) * NUM_OCCURRENCES; i ++) {
            fixtures [i][0][0] = firstTeam;
            fixtures [i][0][1] = teamRotation [0];

            for (int j = 1; j < NUM_TEAMS / 2; j ++) {
                fixtures [i][j][0] = teamRotation [j];
                fixtures [i][j][1] = teamRotation [NUM_TEAMS - j - 1];
            }

            int last = teamRotation [teamRotation.length - 1];

            for (int k = teamRotation.length - 2; k >= 0; k --) {
                teamRotation [k + 1] = teamRotation [k];
            }

            teamRotation [0] = last;
        }
    }

    public static void loadFile () {
        Club [] clubs = new Club [NUM_TEAMS];

        // sim (clubs);
    }

    public static void saveFile () {

    }

    public static void sim (Club [] clubs, int currentWeek, int [][][] fixtures, int currentSeason) {
        String input = "";
        Scanner in = new Scanner(System.in);

        Club [] clubStandings = new Club [NUM_TEAMS];
        System.arraycopy(clubs, 0, clubStandings, 0, NUM_TEAMS);

        while (true) {
            System.out.println ("");

            System.out.println ("(1) Simulate Current Week");
            System.out.println ("(2) Simulate to End of Season");
            System.out.println ("(3) View Fixtures");
            System.out.println ("(4) View Standings");
            System.out.println ("(5) View Club");
            System.out.println ("(6) Save Game");
            System.out.println ("(7) Exit");
            System.out.print ("Please choose an option: ");
            input = in.nextLine();

            switch (input) {
                case "1":
                    simMatches(clubs, fixtures, currentWeek, currentSeason);
                    sortClubs (clubStandings);
                    currentWeek ++;
                    break;
                case "2":
                    while (currentWeek < (NUM_TEAMS - 1) * NUM_OCCURRENCES) {
                        simMatches(clubs, fixtures, currentWeek, currentSeason);
                        sortClubs (clubStandings);
                        currentWeek ++;
                    }
                    break;
                case "3":
                    viewFixtures(fixtures, currentWeek, clubs, currentSeason);
                    break;
                case "4":
                    viewStandings(clubStandings, currentWeek, currentSeason);
                    break;
                case "5":
                    viewClub(clubStandings);
                    break;
                case "6":
                    saveFile();
                    break;
                case "7":
                    System.exit(0);
            }
        }
    }

    public static void simMatches (Club [] clubs, int [][][] fixtures, int currentWeek, int currentSeason) {
        int goals1 = 0;
        int goals2 = 0;

        Random rand = new Random();

        System.out.println ("");
        System.out.println ("Season " + currentSeason + " - Week " + (currentWeek + 1));
        System.out.println ("-------------------");

        for (int i = 0; i < NUM_TEAMS / 2; i ++) {
            goals1 = generateScore(rand.nextInt(SCORING) + 1 + (clubs [fixtures [currentWeek][i][0]].getSway() - 100) - (clubs [fixtures [currentWeek][i][1]].getSway() - 100));
            goals2 = generateScore(rand.nextInt(SCORING) + 1 + (clubs [fixtures [currentWeek][i][1]].getSway() - 100) - (clubs [fixtures [currentWeek][i][0]].getSway() - 100));

            clubs [fixtures [currentWeek][i][0]].setCurrentGoalsFor(clubs [fixtures [currentWeek][i][0]].getCurrentGoalsFor() + goals1);
            clubs [fixtures [currentWeek][i][1]].setCurrentGoalsFor(clubs [fixtures [currentWeek][i][1]].getCurrentGoalsFor() + goals2);

            clubs [fixtures [currentWeek][i][0]].setTotalGoalsFor(clubs [fixtures [currentWeek][i][0]].getTotalGoalsFor() + goals1);
            clubs [fixtures [currentWeek][i][1]].setTotalGoalsFor(clubs [fixtures [currentWeek][i][1]].getTotalGoalsFor() + goals2);

            clubs [fixtures [currentWeek][i][0]].setCurrentGoalsAgainst(clubs [fixtures [currentWeek][i][0]].getCurrentGoalsAgainst() + goals2);
            clubs [fixtures [currentWeek][i][1]].setCurrentGoalsAgainst(clubs [fixtures [currentWeek][i][1]].getCurrentGoalsAgainst() + goals1);

            clubs [fixtures [currentWeek][i][0]].setTotalGoalsAgainst(clubs [fixtures [currentWeek][i][0]].getTotalGoalsAgainst() + goals2);
            clubs [fixtures [currentWeek][i][1]].setTotalGoalsAgainst(clubs [fixtures [currentWeek][i][1]].getTotalGoalsAgainst() + goals1);

            if (goals1 > goals2) {
                clubs [fixtures [currentWeek][i][0]].setCurrentWins(clubs [fixtures [currentWeek][i][0]].getCurrentWins() + 1);
                clubs [fixtures [currentWeek][i][1]].setCurrentLosses(clubs [fixtures [currentWeek][i][1]].getCurrentLosses() + 1);
            } else if (goals1 == goals2) {
                clubs [fixtures [currentWeek][i][0]].setCurrentDraws(clubs [fixtures [currentWeek][i][0]].getCurrentDraws() + 1);
                clubs [fixtures [currentWeek][i][1]].setCurrentDraws(clubs [fixtures [currentWeek][i][1]].getCurrentDraws() + 1);
            } else {
                clubs [fixtures [currentWeek][i][0]].setCurrentLosses(clubs [fixtures [currentWeek][i][0]].getCurrentLosses() + 1);
                clubs [fixtures [currentWeek][i][1]].setCurrentWins(clubs [fixtures [currentWeek][i][1]].getCurrentWins() + 1);
            }

            System.out.println ("Match " + (i + 1) + ": " + clubs [fixtures [currentWeek][i][0]].getName() + " (" + goals1 + " - " + goals2 + ") " + clubs [fixtures [currentWeek][i][1]].getName());
        }
    }

    public static int generateScore (int result) {
        if (result > 99) {
            return 7;
        } else if (result > 96) {
            return 6;
        } else if (result > 90) {
            return 5;
        } else if (result > 80) {
            return 4;
        } else if (result > 65) {
            return 3;
        } else if (result > 40) {
            return 2;
        } else if (result > 15) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void sortClubs (Club [] clubStandings) {
        boolean sortDone = true;

        while (sortDone) {
            sortDone = false;

            for (int i = 0; i < NUM_TEAMS - 1; i ++) {
                if ((clubStandings [i].getCurrentWins() * 3 + clubStandings [i].getCurrentDraws()) < (clubStandings [i + 1].getCurrentWins() * 3 + clubStandings [i + 1].getCurrentDraws())) {
                    Club temp = clubStandings [i];
                    clubStandings [i] = clubStandings [i + 1];
                    clubStandings [i + 1] = temp;

                    sortDone = true;
                } else if ((clubStandings [i].getCurrentWins() * 3 + clubStandings [i].getCurrentDraws()) == (clubStandings [i + 1].getCurrentWins() * 3 + clubStandings [i + 1].getCurrentDraws())) {
                    if ((clubStandings [i].getCurrentGoalsFor() - clubStandings [i].getCurrentGoalsAgainst()) < (clubStandings [i + 1].getCurrentGoalsFor() - clubStandings [i + 1].getCurrentGoalsAgainst())) {
                        Club temp = clubStandings [i];
                        clubStandings [i] = clubStandings [i + 1];
                        clubStandings [i + 1] = temp;

                        sortDone = true;
                    } else if ((clubStandings [i].getCurrentGoalsFor() - clubStandings [i].getCurrentGoalsAgainst()) == (clubStandings [i + 1].getCurrentGoalsFor() - clubStandings [i + 1].getCurrentGoalsAgainst())) {
                        if ((clubStandings [i].getCurrentGoalsFor() < clubStandings [i + 1].getCurrentGoalsFor())) {
                            Club temp = clubStandings [i];
                            clubStandings [i] = clubStandings [i + 1];
                            clubStandings [i + 1] = temp;

                            sortDone = true;
                        }
                    }
                }
            }
        }
    }

    public static void viewFixtures (int [][][] fixtures, int currentWeek, Club [] clubs, int currentSeason) {
        System.out.println ("");
        System.out.println ("Season " + currentSeason + " - Week " + (currentWeek + 1));
        System.out.println ("-----------------------");

        for (int i = 0; i < NUM_TEAMS / 2; i ++) {
            System.out.println ("Match " + (i + 1) + ": " + clubs [fixtures [currentWeek][i][0]].getName() + " vs. " + clubs [fixtures [currentWeek][i][1]].getName());
        }
    }

    public static void viewStandings (Club [] clubStandings, int currentWeek, int currentSeason) {
        System.out.println ("");

        System.out.println ("Season " + currentSeason + " - Week " + (currentWeek + 1));
        System.out.println ("-----------------------");

        System.out.println ("Pos | Team Name |  W |  D |  L |  GF |  GA |   GD | PTS |");
        System.out.println ("----|-----------|----|----|----|-----|-----|------|-----|");

        for (int i = 0; i < NUM_TEAMS; i ++) {
            System.out.print (String.format("%3s", (i + 1)) + " | ");
            System.out.print (String.format("%9s", clubStandings [i].getName()) + " | ");
            System.out.print (String.format("%2s", clubStandings [i].getCurrentWins()) + " | ");
            System.out.print (String.format("%2s", clubStandings [i].getCurrentDraws()) + " | ");
            System.out.print (String.format("%2s", clubStandings [i].getCurrentLosses()) + " | ");
            System.out.print (String.format("%3s", clubStandings [i].getCurrentGoalsFor()) + " | ");
            System.out.print (String.format("%3s", clubStandings [i].getCurrentGoalsAgainst()) + " | ");
            System.out.print (String.format("%4s", (clubStandings [i].getCurrentGoalsFor() - clubStandings [i].getCurrentGoalsAgainst())) + " | ");
            System.out.println (String.format("%3s", (clubStandings [i].getCurrentWins() * 3 + clubStandings [i].getCurrentDraws())) + " | ");
        }
    }

    public static void viewClub (Club [] clubs) {
        int input;
        Scanner in = new Scanner(System.in);

        System.out.println ("");

        System.out.print ("Which team would you like to see?: ");
        input = Integer.parseInt(in.nextLine());
        System.out.println ("");

        System.out.println (clubs [input - 1]);
    }
}