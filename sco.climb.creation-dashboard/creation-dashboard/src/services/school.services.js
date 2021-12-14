import axios from "axios";
export const schoolService = {
    getAllSchools,
    getAllInstitutes
};


function getAllSchools() {
    return axios.get('tmp-data/schools.json', {
        params: {
            size: 1200
        }
    }).then(
        res => {
            if (res) {
                console.log(res)
                return Promise.resolve(res);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function getAllInstitutes() {
    return axios.get('tmp-data/institutes.json', {
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






