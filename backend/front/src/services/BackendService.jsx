import axios from "axios";
import Utils from "../utils/Utils";
import {config} from "@fortawesome/fontawesome-svg-core";
import {alertActions, store} from '../utils/Rdx';
const API_URL = 'http://localhost:8081/api/v1'
const AUTH_URL = 'http://localhost:8081/auth'

// Компонент, который осуществляется авторизацию и лог-аут пользователя через GUI
class BackendService {
    login(login, password) {
        return axios.post(`${AUTH_URL}/login`, {login, password})
    }
    logout() {
        return axios.get(`${AUTH_URL}/logout`, { headers : {Authorization : Utils.getToken()}})
    }

    // Таблица "Страны"
    retrieveAllCountries(page, limit) {
        return axios.get(`${API_URL}/countries?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveCountry(id) {
        return axios.get(`${API_URL}/countries/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createCountry(country) {
        return axios.post(`${API_URL}/countries`, country, { headers : {Authorization : Utils.getToken()}});
    }

    updateCountry(country) {
        return axios.put(`${API_URL}/countries/${country.id}`, country, { headers : {Authorization : Utils.getToken()}});
    }

    deleteCountries(countries) {
        return axios.post(`${API_URL}/deletecountries`, countries, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Художники"
    retrieveAllArtists(page, limit) {
        return axios.get(`${API_URL}/artists?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveArtist(id) {
        return axios.get(`${API_URL}/artists/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createArtist(artist) {
        return axios.post(`${API_URL}/artists`, artist, { headers : {Authorization : Utils.getToken()}});
    }

    updateArtist(artist) {
        return axios.put(`${API_URL}/artists/${artist.id}`, artist, { headers : {Authorization : Utils.getToken()}});
    }

    deleteArtists(artists) {
        return axios.post(`${API_URL}/deleteartists`, artists, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Музеи"
    retrieveAllMuseums(page, limit) {
        return axios.get(`${API_URL}/museums?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveMuseum(id) {
        return axios.get(`${API_URL}/museums/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createMuseum(museum) {
        return axios.post(`${API_URL}/museums`, museum, { headers : {Authorization : Utils.getToken()}});
    }

    updateMuseum(museum) {
        return axios.put(`${API_URL}/museums/${museum.id}`, museum, { headers : {Authorization : Utils.getToken()}});
    }

    deleteMuseums(museums) {
        return axios.post(`${API_URL}/deletemuseums`, museums, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Paintings"
    retrieveAllPaintings(page, limit) {
        return axios.get(`${API_URL}/paintings/?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrievePainting(id) {
        return axios.get(`${API_URL}/paintings/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createPainting(painting) {
        return axios.post(`${API_URL}/paintings`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    updatePainting(painting) {
        return axios.put(`${API_URL}/paintings/${painting.id}`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    deletePaintings(painting) {
        return axios.post(`${API_URL}/deletepaintings`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Users"
    retrieveAllUsers(page, limit) {
        return axios.get(`${API_URL}/users?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveUser(id) {
        return axios.get(`${API_URL}/users/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createUser(user) {
        return axios.post(`${API_URL}/users`, user, { headers : {Authorization : Utils.getToken()}});
    }

    updateUser(user) {
        return axios.put(`${API_URL}/users/${user.id}`, user, { headers : {Authorization : Utils.getToken()}});
    }

    deleteUsers(user) {
        return axios.post(`${API_URL}/deleteusers`, user, { headers : {Authorization : Utils.getToken()}});
    }
}

function showError(msg) {
    store.dispatch(alertActions.error(msg))
}

axios.interceptors.request.use(
    config => {
        store.dispatch(alertActions.clear())
        // Эти строчки приводят к тому, что user null
                //let token = Utils.getToken();
                //if (token)
                //    config.headers.Authorization = token;
        return config;
    },
    error => {
        showError(error.message)
        return Promise.reject(error);

    })



// Здесь вскрылась ещё одна беда:
// при получении ошибки не выводится её название, а пишется стандартное сообщение
// хотя на стороне бэкенда класс написан корректно и путём отладки было установлено, что в 500 ошибку
// записывается данное сообщение. Но на стороне фронтенда мы её не получаем. При чём выполняется только последнее условие
axios.interceptors.response.use(undefined,
    error => {
        if (error.response && error.response.status && [401, 403].indexOf(error.response.status) !== -1) {
            showError("Ошибка авторизации 401/403")
        } else if (error.response && error.response.data && error.response.data.message) {
            showError(error.response.data.message)
        } else {
            showError(error.message)
        }
        return Promise.reject(error);
    })

export default new BackendService()