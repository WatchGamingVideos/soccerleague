const NUM_TEAMS = 20;
const NUM_OCCURRENCES = 2;
const SCORING = 70;
const VARIATION = 30;

let i;
let j;
let k;

const button = document.getElementById ('button');
let modal = document.getElementById ('champion');
let span = document.getElementsByClassName("close")[0];
let modalText = document.getElementById ('modaltext');

let matchResults = new Array ();

for (i = 0 ; i < NUM_TEAMS / 2; i ++) {
    matchResults [i] = new Array ();
}

for (i = 0; i < NUM_TEAMS / 2; i ++) {
    for (j = 1; j < 5; j ++) {
        if (j < 3) {
            matchResults [i].push (document.getElementById ('result' + (i + 1) + 'team' + (j % 3)));
        } else {
            matchResults [i].push (document.getElementById ('result' + (i + 1) + 'score' + (j % 5 - 2)));
        }
    }
}

let upcomingMatches = new Array ();

for (i = 0 ; i < NUM_TEAMS / 2; i ++) {
    upcomingMatches [i] = new Array ();
}

for (i = 0; i < NUM_TEAMS / 2; i ++) {
    for (j = 0; j < 2; j ++) {
        upcomingMatches [i].push (document.getElementById ('upcoming' + (i + 1) + 'team' + (j + 1)));
    }
}

let standings = new Array ();

for (i = 0; i < NUM_TEAMS; i ++) {
    standings [i] = new Array ();
}

for (i = 1; i < NUM_TEAMS + 1; i ++) {
    standings [0].push (document.getElementById ('teamname' + i));
    standings [1].push (document.getElementById ('teamwins' + i));
    standings [2].push (document.getElementById ('teamdraws' + i));
    standings [3].push (document.getElementById ('teamlosses' + i));
    standings [4].push (document.getElementById ('teamgoalsfor' + i));
    standings [5].push (document.getElementById ('teamgoalsagainst' + i));
    standings [6].push (document.getElementById ('teamgoaldifferential' + i));
    standings [7].push (document.getElementById ('teampoints' + i));
}

let clubs = new Array ();
let fixtures = new Array ();
let clubStandings = new Array ();

let currentWeek = 0;
let currentSeason = 1;

for (i = 0; i < (NUM_TEAMS - 1) * NUM_OCCURRENCES; i ++) {
    fixtures [i] = new Array ();

    for (j = 0; j < NUM_TEAMS / 2; j ++) {
        fixtures [i][j] = new Array ();
    }
}

for (i = 0; i < NUM_TEAMS; i ++) {
    clubStandings [i] = i;
}

let teamNames = ["Brighton", "Bayer 04 Leverkusen", "Real Madrid", "PSG",
                "Inter Milan", "PSV", "Sporting CP", "Sturm Graz",
                "Celtic", "Red Star Belgrade", "Club Brugge", "Galatasaray",
                "FC Copenhagen", "Olympiacos", "Molde FK", "Sparta Praha",
                "Shakthar Donetsk", "Qarabag FK", "Dinamo Zagreb", "Young Boys"];

clubs = generateClubs (clubs);
fixtures = generateFixtures (fixtures);

function generateClubs (clubs) {
    for (i = 0; i < NUM_TEAMS; i ++) {
        clubs [i] = {
            id: i,
            name: teamNames [i],
            currentWins: 0,
            currentDraws: 0,
            currentLosses: 0,
            currentGoalsFor: 0,
            currentGoalsAgainst: 0,
            sway: Math.round((Math.random () * VARIATION) + 100 - (VARIATION / 2))
        };
    }

    return clubs;
}

function generateFixtures (fixtures) {
    let firstTeam = 0;
    let teamRotation = new Array ();

    for (i = 0; i < NUM_TEAMS - 1; i ++) {
        teamRotation [i] = i + 1;
    }

    for (i = 0; i < (NUM_TEAMS - 1) * NUM_OCCURRENCES; i ++) {
        fixtures [i][0][0] = firstTeam;
        fixtures [i][0][1] = teamRotation [0];

        for (j = 1; j < NUM_TEAMS / 2; j ++) {
            fixtures [i][j][0] = teamRotation [j];
            fixtures [i][j][1] = teamRotation [NUM_TEAMS - j - 1];
        }

        let last = teamRotation [teamRotation.length - 1];

        for (k = teamRotation.length - 2; k >= 0; k --) {
            teamRotation [k + 1] = teamRotation [k];
        }

        teamRotation [0] = last;
    }

    return fixtures;
}

printFixtures (fixtures, currentWeek, clubs, currentSeason);

button.addEventListener ("click", function () {
    if (currentWeek < (NUM_TEAMS - 1) * NUM_OCCURRENCES) {
        simMatches (clubs, fixtures, currentWeek, currentSeason);
        clubStandings = sortClubs (clubs, clubStandings);
        printStandings (clubs, clubStandings, currentWeek, currentSeason);

        currentWeek ++;

        printFixtures (fixtures, currentWeek, clubs, currentSeason);

        document.getElementById ('week').textContent = 'Current Week: ' + (currentWeek + 1);
    }

    if (button.textContent === 'Next Season') {
        currentWeek = 0;
        currentSeason ++;
        endOfSeason (clubs, clubStandings, currentWeek, currentSeason);
    }

    if (currentWeek >= (NUM_TEAMS - 1) * NUM_OCCURRENCES) {
        modalText.textContent = 'Congratulations to the ' + clubs [clubStandings [0]].name + ' for winning Season ' + currentSeason + '!';
        modal.style.display = "block";
        button.textContent = 'Next Season';
    }
});

span.onclick = function() {
  modal.style.display = "none";
}

window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

function simMatches (clubs, fixtures, currentWeek, currentSeason) {
    let goals1;
    let goals2;

    for (i = 0; i < NUM_TEAMS / 2; i ++) {
        goals1 = generateScore (Math.round ((Math.random () * SCORING) + (clubs [fixtures [currentWeek][i][0]].sway - 100) - (clubs [fixtures [currentWeek][i][1]].sway - 100)));
        goals2 = generateScore (Math.round ((Math.random () * SCORING) + (clubs [fixtures [currentWeek][i][1]].sway - 100) - (clubs [fixtures [currentWeek][i][0]].sway - 100)));

        clubs [fixtures [currentWeek][i][0]].currentGoalsFor += goals1;
        clubs [fixtures [currentWeek][i][1]].currentGoalsFor += goals2;

        clubs [fixtures [currentWeek][i][0]].currentGoalsAgainst += goals2;
        clubs [fixtures [currentWeek][i][1]].currentGoalsAgainst += goals1;

        if (goals1 > goals2) {
            clubs [fixtures [currentWeek][i][0]].currentWins ++;
            clubs [fixtures [currentWeek][i][1]].currentLosses ++;
        } else if (goals1 === goals2) {
            clubs [fixtures [currentWeek][i][0]].currentDraws ++;
            clubs [fixtures [currentWeek][i][1]].currentDraws ++;
        } else {
            clubs [fixtures [currentWeek][i][0]].currentLosses ++;
            clubs [fixtures [currentWeek][i][1]].currentWins ++;
        }

        matchResults [i][0].textContent = clubs [fixtures [currentWeek][i][0]].name;
        matchResults [i][1].textContent = clubs [fixtures [currentWeek][i][1]].name;

        matchResults [i][2].textContent = goals1;
        matchResults [i][3].textContent = goals2;
    }
}

function generateScore (result) {

    if (result > 90) {
        return 7;
    } else if (result > 85) {
        return 6;
    } else if (result > 75) {
        return 5;
    } else if (result > 60) {
        return 4;
    } else if (result > 35) {
        return 3;
    } else if (result > 20) {
        return 2;
    } else if (result > 10) {
        return 1;
    } else {
        return 0;
    }
}

function sortClubs (clubs, clubStandings) {
    let sortDone = true;

    while (sortDone) {
        sortDone = false;

        for (i = 0; i < NUM_TEAMS - 1; i ++) {
            if ((clubs [clubStandings [i]].currentWins * 3 + clubs [clubStandings [i]].currentDraws) < (clubs [clubStandings [i + 1]].currentWins * 3 + clubs [clubStandings [i + 1]].currentDraws)) {
                let temp = clubStandings [i];
                clubStandings [i] = clubStandings [i + 1];
                clubStandings [i + 1] = temp;

                sortDone = true;
            } else if ((clubs [clubStandings [i]].currentWins * 3 + clubs [clubStandings [i]].currentDraws) === (clubs [clubStandings [i + 1]].currentWins * 3 + clubs [clubStandings [i + 1]].currentDraws)) {
                if ((clubs [clubStandings [i]].currentGoalsFor - clubs [clubStandings [i]].currentGoalsAgainst) < (clubs [clubStandings [i + 1]].currentGoalsFor - clubs [clubStandings [i + 1]].currentGoalsAgainst)) {
                    let temp = clubStandings [i];
                    clubStandings [i] = clubStandings [i + 1];
                    clubStandings [i + 1] = temp;

                    sortDone = true;
                } else if ((clubs [clubStandings [i]].currentGoalsFor - clubs [clubStandings [i]].currentGoalsAgainst) === (clubs [clubStandings [i + 1]].currentGoalsFor - clubs [clubStandings [i + 1]].currentGoalsAgainst)) {
                    if ((clubs [clubStandings [i]].currentGoalsFor) < (clubs [clubStandings [i]].currentGoalsFor)) {
                        let temp = clubStandings [i];
                        clubStandings [i] = clubStandings [i + 1];
                        clubStandings [i + 1] = temp;

                        sortDone = true;
                    }
                }
            }
        }
    }

    return clubStandings;
}

function printFixtures (fixtures, currentWeek, clubs, currentSeason) {
    for (i = 0; i < NUM_TEAMS / 2; i ++) {
        if (currentWeek < (NUM_TEAMS - 1) * NUM_OCCURRENCES) {
            upcomingMatches [i][0].textContent = clubs [fixtures [currentWeek][i][0]].name;
            upcomingMatches [i][1].textContent = clubs [fixtures [currentWeek][i][1]].name;
        } else {
            upcomingMatches [i][0].textContent = 'TBD';
            upcomingMatches [i][1].textContent = 'TBD';
        }

    }
}

function printStandings (clubs, clubStandings, currentWeek, currentSeason) {
    for (i = 0; i < NUM_TEAMS; i ++) {
        standings [0][i].textContent = clubs [clubStandings [i]].name;
        standings [1][i].textContent = clubs [clubStandings [i]].currentWins;
        standings [2][i].textContent = clubs [clubStandings [i]].currentDraws;
        standings [3][i].textContent = clubs [clubStandings [i]].currentLosses;
        standings [4][i].textContent = clubs [clubStandings [i]].currentGoalsFor;
        standings [5][i].textContent = clubs [clubStandings [i]].currentGoalsAgainst;
        standings [6][i].textContent = clubs [clubStandings [i]].currentGoalsFor - clubs [clubStandings [i]].currentGoalsAgainst;
        standings [7][i].textContent = clubs [clubStandings [i]].currentWins * 3 + clubs [clubStandings [i]].currentDraws;
    }
}

function endOfSeason (clubs, clubStandings, currentWeek, currentSeason) {
    document.getElementById ('week').textContent = 'Current Week: ' + (currentWeek + 1);
    document.getElementById ('season').textContent = 'Current Season: ' + currentSeason;

    for (i = 0; i < NUM_TEAMS; i ++) {
        clubs [i].currentWins = 0;
        clubs [i].currentDraws = 0;
        clubs [i].currentLosses = 0;
        clubs [i].currentGoalsFor = 0;
        clubs [i].currentGoalsAgainst = 0;
        clubs [i].sway = Math.round((Math.random () * VARIATION) + 100 - (VARIATION / 2));
    }

    printFixtures (fixtures, currentWeek, clubs, currentSeason);
    printStandings (clubs, clubStandings, currentWeek, currentSeason);

    button.textContent = 'Sim Week';
}
