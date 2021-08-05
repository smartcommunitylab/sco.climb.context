import axios from "axios";
export const gameService = {
    getAllMyGames,
    getProposedGames,
    addGame,
    updateGame,
    deleteGame,
    
};

function getAllMyGames() { 
    return axios.get('tmp-data/myGame.json',{
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
function getProposedGames() {
    // todo ...

}

function addGame() {
}
function updateGame() {
}
function deleteGame() {
}




