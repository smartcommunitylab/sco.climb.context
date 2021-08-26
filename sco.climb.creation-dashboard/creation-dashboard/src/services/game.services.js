import axios from "axios";
export const gameService = {
    getAllMyGames,
    getCatalogGames,
    getMinScore,
    getMaxScore,
    addGame,
    updateGame,
    deleteGame,

};

function getAllMyGames() {
    return axios.get('tmp-data/myGame.json', {
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
            var result = Promise.resolve(res.data);
            if (res) {
                if(filter.selectedDisciplines.includes("italiano")){
                    //var json = JSON.parse(res.data);
                    //returns content - pedibusGame - disciplines --> italiano
                    return result;
                }

                if(filter.selectedGeographicArea.includes("mondo")){
                    //var json = JSON.parse(res.data);
                    //returns content - pedibusGame - disciplines --> italiano
                    return result;
                }
                console.log(res.data)
                return result;
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function addGame() {
}
function updateGame() {
}
function deleteGame() {
}




