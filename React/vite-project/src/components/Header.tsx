import React from "react";
import { Navbar, Container, Nav } from "react-bootstrap";
import { Link } from "react-router-dom";
import type { NavItem } from "../type/NavItem";

type HeaderProps = {
  items : NavItem[];
};

const Header: React.FC<HeaderProps> = ({items}) => {
  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        {/* 브랜드 로고도 Link로 변경 */}
        <Navbar.Brand as={Link} to="/">
          MyProject
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            {items.map((item) => 
              item.external ? (
                <Nav.Link
                  key={item.path}
                  href={item.path}
                  target= "_blank"
                  rel="noopener noreferrer"
                >
                  {item.icon} {item.label}
                </Nav.Link>
              ) : (
                <Nav.Link key={item.path} as={Link} to={item.path}>
                   {item.icon} {item.label}
                </Nav.Link>
              )
            )}
            {/* <Button variant="outline-light" className="ms-3">
              Login
            </Button> */}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
