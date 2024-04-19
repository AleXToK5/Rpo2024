import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import NavigationBar from "./components/NavigationBarClass";
import Home from "./components/Home";
import Another_Home from "./components/Another_Home";
import Login from "./components/Login";

function App() {
  return (
      <div className="App">
        {/* Смысл в том, что браузер привязывает компоненты к локальным путям внутри приложения */}
        <BrowserRouter>
          <NavigationBar/>
          <div className="container-fluid">
            <Routes>
              <Route path="home" element={<Home />} />
              {/* Дополнение: создал новый компонент, чтобы разделить то, что выводится с каждой из ссылок */}
              <Route path="Another_Home" element={<Another_Home />} />
              <Route path="login" element={<Login/>} />
            </Routes>
          </div>
        </BrowserRouter>
      </div>
  );
}

export default App;