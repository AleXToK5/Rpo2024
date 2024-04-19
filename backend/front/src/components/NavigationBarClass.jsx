import React from "react";
import {Navbar, Nav} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome, faUser} from "@fortawesome/free-solid-svg-icons";
import {Link} from "react-router-dom";

import {useNavigate} from "react-router-dom"
import Utils from "../utils/Utils";
import axios from "axios";
import BackendService from "../services/BackendService";


class NavigationBarClass extends React.Component {

  constructor(props) {
    super(props);

    // Привязываем к конструктору
    this.goHome = this.goHome.bind(this);
    this.logout = this.logout.bind(this);
  }

  // Метод, который осуществляет переход на другую страницу (Another Home)
  goHome() {
    this.props.navigate('Another_Home');
  }

  logout() {
    BackendService.logout().then(() => {
      Utils.removeUser();
      this.goHome()
    });
  }

  render() {
    let uname = Utils.getUserName();
    return (
        <Navbar bg="light" expand="lg">
          <Navbar.Brand><FontAwesomeIcon icon={faHome}/>{' '} My RPO</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav"/>
          <Navbar.Collapse id="basics-navbar-nav">
            <Nav className="me-auto">
              {/* Переход может осуществляться как напрямую */}
              <Nav.Link as={Link} to="/home">Home</Nav.Link>

              {/* Переход может осуществляться так и при помощи функции*/}
              <Nav.Link onClick={this.goHome}>Another Home</Nav.Link>
            </Nav>

            <Navbar.Text>{uname}</Navbar.Text>
            {
                uname &&
                <Nav.Link onClick={this.logout}><FontAwesomeIcon icon={faUser} fixedWidth/>{' '}Выход</Nav.Link>
            }
            {
                !uname &&
                <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth/>{' '}Вход</Nav.Link>
            }
          </Navbar.Collapse>
        </Navbar>
    );
  }
}

// Свойство, которое получает компонент для навигации и передаёт экземпляру класса
const NavigationBar = props => {
  const navigate = useNavigate()
  return <NavigationBarClass navigate={navigate} {...props} />
}

export default NavigationBar;