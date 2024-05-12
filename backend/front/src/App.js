import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import NavigationBar from "./components/NavigationBarClass";
import Home from "./components/Home";
import Another_Home from "./components/Another_Home";
import Login from "./components/Login";
import Utils from "./utils/Utils";

import {connect} from "react-redux";

const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    return user ? children : <Navigate to={'/login'}/>
};

function App(props) {
    return (
        <div className="App">
            {/* Смысл в том, что браузер привязывает компоненты к локальным путям внутри приложения */}
            <BrowserRouter>
                <NavigationBar/>
                <div className="container-fluid"> {
                            props.error_message &&
                            <div className="alert alert-danger m-1">{props.error_message}</div>
                        }
                    <Routes>
                        <Route path="login" element={<Login/>}/>
                        {/* Не забывай про свою вторую страницу Another Home*/}
                        <Route path="Another_home" element={
                            <ProtectedRoute>
                                <Another_Home/>
                            </ProtectedRoute>
                        }/>

                        <Route path="home" element={
                            <ProtectedRoute>
                                <Home/>
                            </ProtectedRoute>}/>
                    </Routes>
                </div>
            </BrowserRouter>
        </div>
    );
}

const mapStateToProps = function (state) {
    const {msg} = state.alert;
    return {error_message: msg};
}
export default connect(mapStateToProps)(App);