import React from "react";
import Header from "./components/Header";
import Sidebar from "./components/Sidebar";
import MainPage from "./page/MainPage";
import Footer from "./components/Footer";
import type { NavItem } from "./type/NavItem";
import { FaHome, FaInfoCircle, FaCog } from "react-icons/fa";
import { GrServerCluster } from "react-icons/gr";
import { Route, Routes } from "react-router-dom";
import ServerListPage from "./page/SeverListPage";

const navItems: NavItem[] = [
  { label: "홈", path: "/", icon: <FaHome /> },
  { label: "소개", path: "/about", icon: <FaInfoCircle /> },
  { label: "설정", path: "/settings", icon: <FaCog /> },
  { label: "GitHub", path: "https://github.com/", external: true },
];

const sideItems: NavItem[] = [
  { label: "대시보드", path: "/", icon: <FaHome /> },
  { label: "서버 목록", path: "/servers", icon: <GrServerCluster /> },
];

const App: React.FC = () => {
  return (
    <div className="d-flex flex-column" style={{ minHeight: "100vh" }}>
      <Header items={navItems} />

      <div className="d-flex flex-grow-1">
        <Sidebar items={sideItems} />

        <div className="flex-grow-1 d-flex flex-column">
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/servers" element={<ServerListPage />} />
          </Routes>
          <Footer />
        </div>
      </div>
    </div>
  );
};

export default App;
