import axios from "axios";
export const gameService = {
    getAllMyGames,
    getAllActivities,
    getCatalogGames,
    getMinScore,
    getMaxScore,
    addGame,
    updateGame,
    deleteGame,
};


function getAllActivities() {
    return axios.get('tmp-data/activities.json', {
        params: {
            size: 1200
        }
    }).then(
        res => {
            if (res) {
                console.log(res.data)
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function getAllMyGames() {
    return axios.get('tmp-data/myGame.json', {
        params: {
            size: 1200
        }
    }).then(
        res => {
            if (res) {
                // console.log(res.data)
                return Promise.resolve(res);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function getMinScore(game) {
    /*var x = (game.habitsDefinition.habitsData.habits[0].numStud) * (game.habitsDefinition.habitsData.habits[0].chilometri) + (game.habitsDefinition.habitsData.habits[1].numStud) * (game.habitsDefinition.habitsData.habits[1].chilometri) + (game.habitsDefinition.habitsData.habits[2].numStud) * (game.habitsDefinition.habitsData.habits[2].chilometri) + (game.habitsDefinition.habitsData.habits[3].numStud) * (game.habitsDefinition.habitsData.habits[3].chilometri) + (game.habitsDefinition.habitsData.habits[4].numStud) * (game.habitsDefinition.habitsData.habits[4].chilometri) + (game.habitsDefinition.habitsData.habits[5].numStud) * (game.habitsDefinition.habitsData.habits[5].chilometri) + (game.habitsDefinition.habitsData.habits[6].numStud) * (game.habitsDefinition.habitsData.habits[6].chilometri);
    //var y = x*giorni del calendario 
    var k = x; //y
    if (game.habitsDefinition.habitsData.roundtrip == 'si') {
        k = x * 2 // y*2 
    }*/
    console.log(game);
    return 2000;
}


function getMaxScore(game) {
    /* var x = (game.habitsDefinition.habitsData.habits[0].numStud) * (game.habitsDefinition.habitsData.habits[0].chilometri) + (game.habitsDefinition.habitsData.habits[1].numStud) * (game.habitsDefinition.habitsData.habits[1].chilometri) + (game.habitsDefinition.habitsData.habits[2].numStud) * (game.habitsDefinition.habitsData.habits[2].chilometri) + (game.habitsDefinition.habitsData.habits[3].numStud) * (game.habitsDefinition.habitsData.habits[3].chilometri) + (game.habitsDefinition.habitsData.habits[4].numStud) * (game.habitsDefinition.habitsData.habits[4].chilometri) + (game.habitsDefinition.habitsData.habits[5].numStud) * (game.habitsDefinition.habitsData.habits[5].chilometri) + (game.habitsDefinition.habitsData.habits[6].numStud) * (game.habitsDefinition.habitsData.habits[6].chilometri);
     //var y = x*giorni del calendario 
     var k = x; /* y*/
    /* if (game.habitsDefinition.habitsData.roundtrip == 'si') {
         k = x * 2 /* y *2 } */
    console.log(game);
    return 1000;
}


function getCatalogGames(filter) {
    console.log(filter);
    console.log(filter.minScore);
    console.log(filter.maxScore);
    return axios.get('tmp-data/catalogGames.json', {
        params: {
            size: 1200
        }
    }).then(
        res => {
            if (res) {
                // res.data.filter() per filtri
                console.log(res.data)
                return Promise.resolve(returnCatalogFilterGames(res.data));
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }
    )
}


function returnCatalogFilterGames(games) {
    let returnGames = games;
    const arrayDisciplines = games["content"].map(x => x.pedibusGame.disciplines);
    const arrayGeographicArea = games["content"].map(y => y.pedibusGame.geographicArea);
    returnGames.filter = { disciplines: [], geographicArea: [] };
    for (let i = 0; i < arrayDisciplines.length; i++) {
        for (let k = 0; k < arrayDisciplines[i].length; k++) {
            returnGames.filter.disciplines.push({ label: arrayDisciplines[i][k], value: arrayDisciplines[i][k] });
        }
    }
    for (let i = 0; i < arrayGeographicArea.length; i++) {
        for (let k = 0; k < arrayGeographicArea[i].length; k++) {
            returnGames.filter.geographicArea.push({ label: arrayGeographicArea[i][k], value: arrayGeographicArea[i][k] });
        }
    }
    console.log(returnGames);
    return returnGames;
}
function addGame() {
}
function updateGame() {
}
function deleteGame() {
}




